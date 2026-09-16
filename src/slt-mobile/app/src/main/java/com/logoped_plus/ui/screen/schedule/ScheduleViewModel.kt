package com.logoped_plus.ui.screen.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.logoped_plus.domain.model.Lesson
import com.logoped_plus.domain.repository.*
import com.logoped_plus.ui.screen.schedule.model.LessonUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.util.UUID

class ScheduleViewModel(private val lessonRepository: LessonRepository, private val childRepository: ChildRepository) : ViewModel() {
    private val today = LocalDate.now()
    private var confirmed: Lesson? = null
    private val mutableState = MutableStateFlow(ScheduleUiState(
        selectedDate = today, displayedMonth = YearMonth.from(today), displayedWeekStart = today.startOfWeek(),
        childLoadState = childRepository.state.value, lessonLoadState = lessonRepository.state.value,
        children = childRepository.state.value.children
    ))
    val uiState = mutableState.asStateFlow()

    init {
        refresh()
        viewModelScope.launch {
            combine(childRepository.state, lessonRepository.state) { _, _ -> Unit }
                .collect { refresh() }
        }
    }

    private fun refresh(children: ChildLoadState = childRepository.state.value, loaded: LessonLoadState = lessonRepository.state.value) {
        val committed = confirmed
        if (loaded is LessonLoadState.Ready && committed != null && loaded.lessons.any { it == committed }) confirmed = null
        val overlay = confirmed
        val rows = if (overlay == null) loaded.lessons else if (loaded.lessons.any { it.id == overlay.id })
            loaded.lessons.map { if (it.id == overlay.id) overlay else it } else loaded.lessons + overlay
        val state = mutableState.value
        val models = rows.map { toUiModel(it, children) }
        // A missing row must not discard a draft. Names can change independently of its raw input.
        val selected = state.editor?.takeIf { it.mode != LessonEditorMode.CREATE }?.original?.let { toUiModel(it, children) }
        mutableState.value = state.copy(childLoadState = children, lessonLoadState = loaded, children = children.children,
            lessons = models, selectedLesson = selected, awaitingSnapshot = confirmed != null)
    }

    fun onAction(action: ScheduleAction) {
        if (mutableState.value.saving) return
        val state = mutableState.value
        when (action) {
            ScheduleAction.RetryChildren -> childRepository.retryLoading()
            ScheduleAction.RetryLessons -> lessonRepository.retryLoading()
            is ScheduleAction.StartCreatingLesson -> {
                val date = action.scheduledAt ?: state.selectedDate.atStartOfDay()
                setEditor(LessonEditorState(mode = LessonEditorMode.CREATE, epochDay = date.toLocalDate().toEpochDay(),
                    hour = date.hour.toString().padStart(2, '0'), minute = date.minute.toString().padStart(2, '0')))
            }
            is ScheduleAction.SelectLesson -> {
                val lesson = confirmed?.takeIf { it.id == action.lessonId }
                    ?: lessonRepository.state.value.lessons.find { it.id == action.lessonId } ?: return
                setEditor(LessonEditorState.from(lesson))
            }
            ScheduleAction.StartEditingLesson -> {
                val editor = state.editor?.takeIf { it.mode == LessonEditorMode.DETAILS } ?: return
                mutableState.value = state.copy(detailsDraft = editor)
                setEditor(editor.copy(sessionId = UUID.randomUUID().toString(), mode = LessonEditorMode.EDIT, status = LessonEditorStatus.Idle), keepDetails = true)
            }
            ScheduleAction.CancelEditingLesson -> setEditor(state.detailsDraft)
            ScheduleAction.CloseLesson, ScheduleAction.CancelCreatingLesson -> setEditor(null)
            is ScheduleAction.ChangeText -> change(action.sessionId) { editor ->
                if (editor.mode == LessonEditorMode.DETAILS && action.field != LessonTextField.COMMENT) editor
                else when (action.field) {
                    LessonTextField.HOUR -> editor.copy(hour = action.value)
                    LessonTextField.MINUTE -> editor.copy(minute = action.value)
                    LessonTextField.DURATION -> editor.copy(duration = action.value)
                    LessonTextField.COMMENT -> if (editor.mode == LessonEditorMode.CREATE) editor else editor.copy(comment = action.value)
                }
            }
            is ScheduleAction.ChangeDate -> change(action.sessionId) { if (it.mode == LessonEditorMode.DETAILS) it else it.copy(epochDay = action.date.toEpochDay()) }
            is ScheduleAction.ChangeChildren -> change(action.sessionId) { if (it.mode == LessonEditorMode.DETAILS) it else it.copy(childIds = action.ids.distinct()) }
            is ScheduleAction.ChangeVideos -> change(action.sessionId) { if (it.mode == LessonEditorMode.CREATE) it else it.copy(videoUris = action.uris.distinct()) }
            is ScheduleAction.ConfirmLesson -> confirm(action.sessionId)
            is ScheduleAction.SelectDate -> mutableState.value = state.copy(selectedDate = action.date)
            is ScheduleAction.ChangeViewMode -> mutableState.value = state.copy(viewMode = action.mode,
                displayedMonth = YearMonth.from(state.selectedDate), displayedWeekStart = state.selectedDate.startOfWeek())
            ScheduleAction.PreviousMonth -> mutableState.value = state.copy(displayedMonth = state.displayedMonth.minusMonths(1))
            ScheduleAction.NextMonth -> mutableState.value = state.copy(displayedMonth = state.displayedMonth.plusMonths(1))
            ScheduleAction.PreviousWeek -> mutableState.value = state.copy(displayedWeekStart = state.displayedWeekStart.minusWeeks(1))
            ScheduleAction.NextWeek -> mutableState.value = state.copy(displayedWeekStart = state.displayedWeekStart.plusWeeks(1))
        }
    }

    private fun change(sessionId: String, transform: (LessonEditorState) -> LessonEditorState) {
        val editor = mutableState.value.editor?.takeIf { it.sessionId == sessionId } ?: return
        setEditor(transform(editor).copy(status = LessonEditorStatus.Idle), keepDetails = true)
    }

    private fun setEditor(editor: LessonEditorState?, keepDetails: Boolean = false) {
        mutableState.value = mutableState.value.copy(editor = editor,
            detailsDraft = if (keepDetails) mutableState.value.detailsDraft else null,
            isCreatingLesson = editor?.mode == LessonEditorMode.CREATE,
            isEditingLesson = editor?.mode == LessonEditorMode.EDIT,
            creationDateTime = if (editor?.mode == LessonEditorMode.CREATE) LocalDate.ofEpochDay(editor.epochDay).atStartOfDay() else null)
        refresh()
    }

    private fun confirm(sessionId: String) {
        // Read repository readiness now, even when its collector has not been dispatched yet.
        refresh()
        val state = mutableState.value
        val editor = state.editor?.takeIf { it.sessionId == sessionId } ?: return
        if (!state.canSave) return
        if (editor.mode == LessonEditorMode.DETAILS && !editor.changed) return
        val lesson = editor.toLesson() ?: return
        setEditor(editor.copy(status = LessonEditorStatus.Saving), keepDetails = true)
        viewModelScope.launch {
            val result = if (editor.mode == LessonEditorMode.CREATE) lessonRepository.addLesson(lesson) else lessonRepository.updateLesson(lesson)
            when (result) {
                is LessonWriteResult.Failure -> setEditor(editor.copy(status = LessonEditorStatus.Failure(result.reason)), keepDetails = true)
                is LessonWriteResult.Success -> {
                    confirmed = result.lesson
                    setEditor(if (editor.mode == LessonEditorMode.CREATE) null else LessonEditorState.from(result.lesson))
                }
            }
        }
    }

    private fun toUiModel(lesson: Lesson, children: ChildLoadState): LessonUiModel {
        val byId = children.children.associateBy { it.id }
        return LessonUiModel(lesson.id, lesson.scheduledAt, lesson.durationMinutes,
            lesson.childIds.mapNotNull { byId[it]?.name }.joinToString(", "), lesson.comment, lesson.childIds,
            lesson.videoAttachments.map { it.uri })
    }
}
