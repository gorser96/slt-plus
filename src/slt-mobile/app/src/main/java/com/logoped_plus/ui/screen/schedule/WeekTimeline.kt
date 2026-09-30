package com.logoped_plus.ui.screen.schedule

import com.logoped_plus.ui.screen.schedule.model.LessonUiModel
import java.time.Duration
import java.time.LocalDate

internal data class WeekLessonSpan(
    val lesson: LessonUiModel,
    val startMinute: Float,
    val endMinute: Float,
    val lane: Int? = null
) {
    fun occupiesHour(hour: Int): Boolean = startMinute < (hour + 1) * 60 && endMinute > hour * 60
}

internal data class WeekExtraGroup(
    val spans: List<WeekLessonSpan>,
    val topMinute: Float
)

internal data class WeekDayTimeline(
    val visibleSpans: List<WeekLessonSpan>,
    val extraGroups: List<WeekExtraGroup>
) {
    fun isHourEmpty(hour: Int): Boolean =
        visibleSpans.none { it.occupiesHour(hour) } &&
            extraGroups.none { group -> group.spans.any { it.occupiesHour(hour) } }
}

internal fun buildWeekDayTimeline(date: LocalDate, lessons: List<LessonUiModel>): WeekDayTimeline {
    val dayStart = date.atStartOfDay()
    val dayEnd = dayStart.plusDays(1)
    // Stable start-time order, including parts of lessons continued from the previous day.
    val spans = lessons
        .sortedBy { it.scheduledAt }
        .mapNotNull { lesson ->
            val end = lesson.scheduledAt.plusMinutes(lesson.durationMinutes.toLong())
            if (lesson.durationMinutes <= 0 || lesson.scheduledAt >= dayEnd || end <= dayStart) {
                null
            } else {
                WeekLessonSpan(
                    lesson,
                    Duration.between(dayStart, maxOf(dayStart, lesson.scheduledAt)).seconds / 60f,
                    Duration.between(dayStart, minOf(dayEnd, end)).seconds / 60f
                )
            }
        }

    val visibleSpans = mutableListOf<WeekLessonSpan>()
    val extraGroups = mutableListOf<WeekExtraGroup>()
    var cluster = emptyList<WeekLessonSpan>()
    var clusterMaxEnd = 0f

    fun flushCluster() {
        if (cluster.size == 1) {
            visibleSpans += cluster.first()
        } else if (cluster.size >= 2) {
            visibleSpans += cluster.first().copy(lane = 0)
            visibleSpans += cluster[1].copy(lane = 1)
            val hidden = cluster.drop(2)
            if (hidden.isNotEmpty()) {
                extraGroups += WeekExtraGroup(hidden, hidden.first().startMinute)
            }
        }
        cluster = emptyList()
    }

    spans.forEach { span ->
        // Strict overlap: touching edges (start == maxEnd) start a new cluster.
        if (cluster.isNotEmpty() && span.startMinute < clusterMaxEnd) {
            cluster += span
            clusterMaxEnd = maxOf(clusterMaxEnd, span.endMinute)
        } else {
            flushCluster()
            cluster = listOf(span)
            clusterMaxEnd = span.endMinute
        }
    }
    flushCluster()

    return WeekDayTimeline(visibleSpans, extraGroups)
}
