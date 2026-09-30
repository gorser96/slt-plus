package com.logoped_plus.ui.screen.schedule

import com.logoped_plus.ui.screen.schedule.model.LessonUiModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class WeekTimelineTest {

    private val date = LocalDate.of(2026, 9, 21)
    private val nextDay = date.plusDays(1)

    @Test
    fun singleLessonTakesFullWidthWithoutGroup() {
        val timeline = buildWeekDayTimeline(date, listOf(lesson("a", 9, 0, 60)))

        val span = timeline.visibleSpans.single()
        assertEquals("a", span.lesson.id)
        assertNull(span.lane)
        assertEquals(540f, span.startMinute, 0.001f)
        assertEquals(600f, span.endMinute, 0.001f)
        assertTrue(timeline.extraGroups.isEmpty())
        assertTrue(timeline.isHourEmpty(8))
        assertFalse(timeline.isHourEmpty(9))
        assertTrue(timeline.isHourEmpty(10))
    }

    @Test
    fun twoOverlappingLessonsBecomeColumnsInStartOrder() {
        val timeline = buildWeekDayTimeline(date, listOf(
            lesson("b", 9, 30, 30),
            lesson("a", 9, 0, 120)
        ))

        assertEquals(listOf("a", "b"), timeline.visibleSpans.map { it.lesson.id })
        assertEquals(0, timeline.visibleSpans[0].lane)
        assertEquals(1, timeline.visibleSpans[1].lane)
        assertTrue(timeline.extraGroups.isEmpty())
        assertFalse(timeline.isHourEmpty(9))
        assertFalse(timeline.isHourEmpty(10))
    }

    @Test
    fun threeOverlappingLessonsKeepFirstTwoColumnsAndHideTheRest() {
        val timeline = buildWeekDayTimeline(date, listOf(
            lesson("c", 10, 0, 90),
            lesson("b", 9, 30, 60),
            lesson("a", 9, 0, 120)
        ))

        assertEquals(listOf("a", "b"), timeline.visibleSpans.map { it.lesson.id })
        assertEquals(0, timeline.visibleSpans[0].lane)
        assertEquals(1, timeline.visibleSpans[1].lane)

        val group = timeline.extraGroups.single()
        assertEquals(listOf("c"), group.spans.map { it.lesson.id })
        assertEquals(600f, group.topMinute, 0.001f)
        assertFalse(timeline.isHourEmpty(11))
    }

    @Test
    fun touchingIntervalsAreNotOverlapping() {
        val timeline = buildWeekDayTimeline(date, listOf(
            lesson("a", 9, 0, 30),
            lesson("b", 9, 30, 30)
        ))

        assertEquals(listOf("a", "b"), timeline.visibleSpans.map { it.lesson.id })
        assertNull(timeline.visibleSpans[0].lane)
        assertNull(timeline.visibleSpans[1].lane)
        assertTrue(timeline.extraGroups.isEmpty())
    }

    @Test
    fun chainedOverlapsFormOneGroup() {
        val timeline = buildWeekDayTimeline(date, listOf(
            lesson("a", 9, 0, 60),
            lesson("b", 9, 30, 60),
            lesson("c", 10, 15, 45)
        ))

        assertEquals(listOf("a", "b"), timeline.visibleSpans.map { it.lesson.id })
        assertEquals(0, timeline.visibleSpans[0].lane)
        assertEquals(1, timeline.visibleSpans[1].lane)

        val group = timeline.extraGroups.single()
        assertEquals(listOf("c"), group.spans.map { it.lesson.id })
        assertEquals(615f, group.topMinute, 0.001f)
    }

    @Test
    fun midnightSplitKeepsDayClusteringIndependent() {
        val night = LessonUiModel(
            id = "night",
            scheduledAt = date.atTime(23, 30),
            durationMinutes = 60,
            childNames = "night",
            comment = ""
        )

        val firstDay = buildWeekDayTimeline(date, listOf(
            night,
            lesson("late", 23, 45, 30)
        ))
        assertEquals(listOf("night", "late"), firstDay.visibleSpans.map { it.lesson.id })
        assertEquals(0, firstDay.visibleSpans[0].lane)
        assertEquals(1, firstDay.visibleSpans[1].lane)
        assertTrue(firstDay.extraGroups.isEmpty())

        val secondDay = buildWeekDayTimeline(nextDay, listOf(
            night,
            lesson("early", 0, 15, 45, nextDay)
        ))
        assertEquals(listOf("night", "early"), secondDay.visibleSpans.map { it.lesson.id })
        assertEquals(0f, secondDay.visibleSpans[0].startMinute, 0.001f)
        assertEquals(30f, secondDay.visibleSpans[0].endMinute, 0.001f)
        assertEquals(0, secondDay.visibleSpans[0].lane)
        assertEquals(1, secondDay.visibleSpans[1].lane)
        assertTrue(secondDay.extraGroups.isEmpty())
    }

    private fun lesson(
        id: String,
        hour: Int,
        minute: Int,
        durationMinutes: Int,
        day: LocalDate = date
    ): LessonUiModel = LessonUiModel(
        id = id,
        scheduledAt = day.atTime(LocalTime.of(hour, minute)),
        durationMinutes = durationMinutes,
        childNames = id,
        comment = ""
    )
}
