package com.logoped_plus.ui.screen.schedule

import com.logoped_plus.ui.screen.schedule.model.LessonUiModel
import java.time.Duration
import java.time.LocalDate

internal data class WeekLessonSpan(
    val lesson: LessonUiModel,
    val startMinute: Float,
    val endMinute: Float
) {
    fun occupiesHour(hour: Int): Boolean = startMinute < (hour + 1) * 60 && endMinute > hour * 60
}

internal data class WeekDayTimeline(val spans: List<WeekLessonSpan>) {
    // Keep input order, including when a lesson continues from the previous day.
    val visibleSpans: List<WeekLessonSpan> = buildList {
        spans.forEach { span ->
            if (none { shown -> (0..23).any { shown.occupiesHour(it) && span.occupiesHour(it) } }) {
                add(span)
            }
        }
    }
    val extraSpans = spans.filter { it !in visibleSpans }

    fun isHourEmpty(hour: Int): Boolean = spans.none { it.occupiesHour(hour) }
}

internal fun buildWeekDayTimeline(date: LocalDate, lessons: List<LessonUiModel>): WeekDayTimeline {
    val dayStart = date.atStartOfDay()
    val dayEnd = dayStart.plusDays(1)
    return WeekDayTimeline(lessons.mapNotNull { lesson ->
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
    })
}
