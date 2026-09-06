package com.logoped_plus.ui.screen.schedule

import java.time.LocalDate
import java.time.LocalDateTime

sealed interface ScheduleAction {

    data class SelectDate(
        val date: LocalDate
    ) : ScheduleAction

    data class SelectLesson(
        val lessonId: String
    ) : ScheduleAction

    data object CloseLesson : ScheduleAction

    data object StartEditingLesson : ScheduleAction

    data object CancelEditingLesson : ScheduleAction

    data class UpdateLesson(
        val lessonId: String,
        val scheduledAt: LocalDateTime,
        val childIds: List<String>,
        val durationMinutes: Int,
        val comment: String,
        val videoUris: List<String>
    ) : ScheduleAction

    data class StartCreatingLesson(val scheduledAt: LocalDateTime? = null) : ScheduleAction

    data object CancelCreatingLesson : ScheduleAction

    data class CreateLesson(
        val scheduledAt: LocalDateTime,
        val childIds: List<String>,
        val durationMinutes: Int
    ) : ScheduleAction

    data class ChangeViewMode(
        val mode: ScheduleViewMode
    ) : ScheduleAction

    data object PreviousMonth : ScheduleAction

    data object NextMonth : ScheduleAction

    data object PreviousWeek : ScheduleAction

    data object NextWeek : ScheduleAction
}
