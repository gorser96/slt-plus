package com.logoped_plus.ui.screen.schedule

import com.logoped_plus.domain.model.Child
import com.logoped_plus.ui.screen.schedule.model.LessonUiModel
import java.time.LocalDateTime
import java.time.LocalDate
import java.time.YearMonth

data class ScheduleUiState(
    val selectedLesson: LessonUiModel? = null,
    val isCreatingLesson: Boolean = false,
    val creationDateTime: LocalDateTime? = null,
    val isEditingLesson: Boolean = false,
    val viewMode: ScheduleViewMode = ScheduleViewMode.MONTH,
    val selectedDate: LocalDate,
    val displayedMonth: YearMonth,
    val displayedWeekStart: LocalDate,
    val lessons: List<LessonUiModel> = emptyList(),
    val children: List<Child> = emptyList()
)
