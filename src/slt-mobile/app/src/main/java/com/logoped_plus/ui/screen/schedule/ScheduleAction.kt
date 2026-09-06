package com.logoped_plus.ui.screen.schedule

import com.logoped_plus.domain.model.Lesson
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

    data object StartCreatingLesson : ScheduleAction

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