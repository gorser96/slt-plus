package com.logoped_plus.ui.screen.schedule

import com.logoped_plus.domain.model.Child
import com.logoped_plus.domain.repository.ChildLoadState
import com.logoped_plus.domain.repository.LessonLoadState
import com.logoped_plus.ui.screen.schedule.model.LessonUiModel
import java.time.LocalDateTime
import java.time.LocalDate
import java.time.YearMonth

data class ScheduleUiState(
    val lessonLoadState: LessonLoadState = LessonLoadState.Loading(),
    val editor: LessonEditorState? = null,
    val detailsDraft: LessonEditorState? = null,
    val awaitingSnapshot: Boolean = false,
    val childLoadState: ChildLoadState = ChildLoadState.Loading(),
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
) {
    val saving: Boolean get() = editor?.saving == true
    val canSave: Boolean get() = !saving && !awaitingSnapshot &&
        childLoadState is ChildLoadState.Ready && lessonLoadState is LessonLoadState.Ready &&
        editor?.toLesson()?.let { lesson ->
            lesson.childIds.all { id -> children.any { it.id == id } } &&
                (editor.mode == LessonEditorMode.CREATE || lessonLoadState.lessons.any { it.id == lesson.id })
        } == true
}
