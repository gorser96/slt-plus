package com.logoped_plus.ui.screen.schedule

import com.logoped_plus.domain.model.Lesson
import java.time.LocalDate

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
        val lesson: Lesson
    ) : ScheduleAction

    data class ChangeViewMode(
        val mode: ScheduleViewMode
    ) : ScheduleAction

    data object PreviousMonth : ScheduleAction

    data object NextMonth : ScheduleAction

    data object PreviousWeek : ScheduleAction

    data object NextWeek : ScheduleAction
}