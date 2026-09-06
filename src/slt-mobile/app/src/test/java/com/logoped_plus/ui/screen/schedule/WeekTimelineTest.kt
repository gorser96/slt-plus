package com.logoped_plus.ui.screen.schedule

import com.logoped_plus.ui.screen.schedule.model.LessonUiModel
import java.time.LocalDate
import org.junit.Assert.*
import org.junit.Test

class WeekTimelineTest {
    private val date = LocalDate.of(2026, 9, 7)

    private fun lesson(id: String, hour: Int, minute: Int, duration: Int) = LessonUiModel(
        id, date.atTime(hour, minute), duration, "Ребёнок", ""
    )

    @Test fun ninetyMinutesUsesOneAndAHalfHoursWithMinuteOffset() {
        val timeline = buildWeekDayTimeline(date, listOf(lesson("a", 9, 30, 90)))
        val span = timeline.visibleSpans.single()
        assertEquals(570f, span.startMinute)
        assertEquals(90f, span.endMinute - span.startMinute)
        assertFalse(timeline.isHourEmpty(9))
        assertFalse(timeline.isHourEmpty(10))
        assertTrue(timeline.isHourEmpty(11))
    }

    @Test fun collisionsKeepFirstInputAndIncludeContinuingLessons() {
        val first = lesson("first", 9, 30, 90)
        val second = lesson("second", 10, 0, 120)
        val timeline = buildWeekDayTimeline(date, listOf(first, second))
        assertEquals(listOf(first), timeline.visibleSpans.map { it.lesson })
        assertEquals(listOf(second), timeline.extraSpans.map { it.lesson })
        assertFalse(timeline.isHourEmpty(11))
        assertTrue(timeline.isHourEmpty(12))
    }

    @Test fun sameHourStillCollapsesAndAdjacentHoursDoNot() {
        val timeline = buildWeekDayTimeline(date, listOf(
            lesson("a", 9, 0, 30), lesson("b", 9, 30, 30), lesson("c", 10, 0, 60)
        ))
        assertEquals(listOf("a", "c"), timeline.visibleSpans.map { it.lesson.id })
        assertEquals(listOf("b"), timeline.extraSpans.map { it.lesson.id })
    }

    @Test fun midnightClipsBothDaysAndDoesNotOccupyFollowingHour() {
        val overnight = lesson("night", 23, 30, 90)
        val today = buildWeekDayTimeline(date, listOf(overnight)).visibleSpans.single()
        val tomorrow = buildWeekDayTimeline(date.plusDays(1), listOf(overnight))
        assertEquals(1410f, today.startMinute)
        assertEquals(1440f, today.endMinute)
        assertEquals(0f, tomorrow.visibleSpans.single().startMinute)
        assertEquals(60f, tomorrow.visibleSpans.single().endMinute)
        assertFalse(tomorrow.isHourEmpty(0))
        assertTrue(tomorrow.isHourEmpty(1))
        assertTrue(buildWeekDayTimeline(date.minusDays(1), listOf(overnight)).spans.isEmpty())
    }
}
