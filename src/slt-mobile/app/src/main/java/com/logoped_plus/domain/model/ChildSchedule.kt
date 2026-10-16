package com.logoped_plus.domain.model

import java.time.LocalDate
import java.time.LocalTime

data class WeeklyLessonSlot(
    val id: String,
    val dayOfWeek: Int,
    val startTime: LocalTime,
    val durationMinutes: Int
)

data class ChildSchedule(
    val slots: List<WeeklyLessonSlot> = emptyList(),
    val endDate: LocalDate? = null
) {
    val isEmpty: Boolean get() = slots.isEmpty()
}
