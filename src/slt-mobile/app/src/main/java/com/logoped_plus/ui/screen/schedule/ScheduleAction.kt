package com.logoped_plus.ui.screen.schedule

import java.time.LocalDate
import java.time.LocalDateTime

sealed interface ScheduleAction {
    data object RetryChildren : ScheduleAction
    data object RetryLessons : ScheduleAction
    data class SelectDate(val date: LocalDate) : ScheduleAction
    data class SelectLesson(val lessonId: String) : ScheduleAction
    data object CloseLesson : ScheduleAction
    data object StartEditingLesson : ScheduleAction
    data object CancelEditingLesson : ScheduleAction
    data class StartCreatingLesson(val scheduledAt: LocalDateTime? = null) : ScheduleAction
    data object CancelCreatingLesson : ScheduleAction
    data class ChangeText(val sessionId: String, val field: LessonTextField, val value: String) : ScheduleAction
    data class ChangeDate(val sessionId: String, val date: LocalDate) : ScheduleAction
    data class ChangeChildren(val sessionId: String, val ids: List<String>) : ScheduleAction
    data class ChangeVideos(val sessionId: String, val uris: List<String>) : ScheduleAction
    data class ConfirmLesson(val sessionId: String) : ScheduleAction
    data class ChangeViewMode(val mode: ScheduleViewMode) : ScheduleAction
    data object PreviousMonth : ScheduleAction
    data object NextMonth : ScheduleAction
    data object PreviousWeek : ScheduleAction
    data object NextWeek : ScheduleAction
}
