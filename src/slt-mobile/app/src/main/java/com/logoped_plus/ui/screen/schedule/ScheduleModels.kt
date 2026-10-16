package com.logoped_plus.ui.screen.schedule

import java.time.LocalDate

enum class ScheduleViewMode {
    MONTH,
    WEEK,
    DAY
}

fun LocalDate.startOfWeek(): LocalDate {
    return minusDays((dayOfWeek.value - 1).toLong())
}

fun effectiveGridRange(startHour: Int, endHour: Int): IntRange {
    return if (startHour in 0..23 && endHour in startHour + 1..24) startHour until endHour else 0..23
}