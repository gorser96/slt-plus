package com.logoped_plus.ui.screen.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.logoped_plus.domain.model.Lesson
import com.logoped_plus.domain.model.VideoAttachment
import com.logoped_plus.domain.repository.ChildRepository
import com.logoped_plus.domain.repository.LessonRepository
import com.logoped_plus.ui.screen.schedule.model.LessonUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalDate
import java.time.YearMonth

class ScheduleViewModel(
    private val lessonRepository: LessonRepository,
    private val childRepository: ChildRepository
) : ViewModel() {

    private val today = LocalDate.now()

    private val _uiState = MutableStateFlow(
        ScheduleUiState(
            selectedDate = today,
            displayedMonth = YearMonth.from(today),
            displayedWeekStart = today.startOfWeek(),
            lessons = lessonRepository.getLessons().map { it.toUiModel() },
            children = childRepository.children.value
        )
    )

    val uiState: StateFlow<ScheduleUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            childRepository.children.collectLatest { children ->
                _uiState.update { state ->
                    val lessons = lessonRepository
                        .getLessons()
                        .map { it.toUiModel() }

                    state.copy(
                        children = children,
                        lessons = lessons,
                        selectedLesson = state.selectedLesson
                            ?.let { selectedLesson ->
                                lessons.find { it.id == selectedLesson.id }
                            }
                    )
                }
            }
        }
    }

    fun onAction(action: ScheduleAction) {
        when (action) {
            ScheduleAction.StartEditingLesson -> {
                _uiState.update { it.copy(isEditingLesson = it.selectedLesson != null) }
            }

            ScheduleAction.CancelEditingLesson -> {
                _uiState.update { it.copy(isEditingLesson = false) }
            }

            is ScheduleAction.UpdateLesson -> {
                val original = lessonRepository.getLessonById(action.lessonId) ?: return
                if (action.durationMinutes <= 0 || action.childIds.isEmpty()) return
                lessonRepository.updateLesson(original.copy(
                    scheduledAt = action.scheduledAt,
                    childIds = action.childIds,
                    durationMinutes = action.durationMinutes,
                    comment = action.comment,
                    videoAttachments = action.videoUris.map { VideoAttachment(it) }
                ))
                val lessons = lessonRepository.getLessons().map { it.toUiModel() }
                _uiState.update { it.copy(
                    lessons = lessons,
                    selectedLesson = lessons.find { it.id == action.lessonId },
                    isEditingLesson = false
                ) }
            }

            is ScheduleAction.SelectDate -> {
                selectDate(action.date)
            }

            is ScheduleAction.SelectLesson -> {
                selectLesson(action.lessonId)
            }

            ScheduleAction.CloseLesson -> {
                closeLesson()
            }

            is ScheduleAction.StartCreatingLesson -> {
                startCreatingLesson(action.scheduledAt)
            }

            ScheduleAction.CancelCreatingLesson -> {
                cancelCreatingLesson()
            }

            is ScheduleAction.CreateLesson -> {
                createLesson(
                    Lesson(
                        scheduledAt = action.scheduledAt,
                        durationMinutes = action.durationMinutes,
                        childIds = action.childIds
                    )
                )
            }

            is ScheduleAction.ChangeViewMode -> {
                changeViewMode(action.mode)
            }

            ScheduleAction.PreviousMonth -> {
                showPreviousMonth()
            }

            ScheduleAction.NextMonth -> {
                showNextMonth()
            }

            ScheduleAction.PreviousWeek -> {
                showPreviousWeek()
            }

            ScheduleAction.NextWeek -> {
                showNextWeek()
            }
        }
    }

    private fun Lesson.toUiModel(): LessonUiModel {
        return LessonUiModel(
            id = id,
            scheduledAt = scheduledAt,
            durationMinutes = durationMinutes,
            childNames = childIds
                .mapNotNull { childRepository.getChildById(it)?.name }
                .joinToString(", "),
            comment = comment,
            childIds = childIds,
            videoUris = videoAttachments.map { it.uri }
        )
    }

    private fun selectDate(date: LocalDate) {
        _uiState.update {
            it.copy(selectedDate = date)
        }
    }

    private fun selectLesson(lessonId: String) {
        _uiState.update { state ->
            state.copy(
                selectedLesson = state.lessons.find { it.id == lessonId }
            )
        }
    }

    private fun closeLesson() {
        _uiState.update {
            it.copy(selectedLesson = null, isEditingLesson = false)
        }
    }

    private fun startCreatingLesson(scheduledAt: LocalDateTime?) {
        _uiState.update {
            it.copy(
                isCreatingLesson = true,
                creationDateTime = scheduledAt ?: it.selectedDate.atStartOfDay()
            )
        }
    }

    private fun cancelCreatingLesson() {
        _uiState.update {
            it.copy(isCreatingLesson = false, creationDateTime = null)
        }
    }

    private fun createLesson(lesson: Lesson) {
        lessonRepository.addLesson(lesson)

        _uiState.update {
            it.copy(
                lessons = lessonRepository.getLessons().map { lesson -> lesson.toUiModel() },
                isCreatingLesson = false,
                creationDateTime = null
            )
        }
    }

    private fun changeViewMode(mode: ScheduleViewMode) {
        _uiState.update { state ->
            when (mode) {
                ScheduleViewMode.MONTH -> {
                    state.copy(
                        viewMode = mode,
                        displayedMonth = YearMonth.from(state.selectedDate)
                    )
                }

                ScheduleViewMode.WEEK -> {
                    state.copy(
                        viewMode = mode,
                        displayedWeekStart = state.selectedDate.startOfWeek()
                    )
                }
            }
        }
    }

    private fun showPreviousMonth() {
        _uiState.update {
            it.copy(
                displayedMonth = it.displayedMonth.minusMonths(1)
            )
        }
    }

    private fun showNextMonth() {
        _uiState.update {
            it.copy(
                displayedMonth = it.displayedMonth.plusMonths(1)
            )
        }
    }

    private fun showPreviousWeek() {
        _uiState.update {
            it.copy(
                displayedWeekStart = it.displayedWeekStart.minusWeeks(1)
            )
        }
    }

    private fun showNextWeek() {
        _uiState.update {
            it.copy(
                displayedWeekStart = it.displayedWeekStart.plusWeeks(1)
            )
        }
    }
}
