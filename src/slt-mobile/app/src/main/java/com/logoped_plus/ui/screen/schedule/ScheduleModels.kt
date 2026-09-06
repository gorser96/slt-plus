package com.logoped_plus.ui.screen.schedule

import java.time.LocalDate

enum class ScheduleViewMode {
    MONTH,
    WEEK
}

fun LocalDate.startOfWeek(): LocalDate {
    return minusDays((dayOfWeek.value - 1).toLong())
}