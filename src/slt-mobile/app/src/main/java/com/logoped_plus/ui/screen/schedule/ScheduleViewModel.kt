package com.logoped_plus.ui.screen.schedule

import androidx.lifecycle.ViewModel
import com.logoped_plus.domain.model.Lesson
import com.logoped_plus.domain.repository.ChildRepository
import com.logoped_plus.domain.repository.LessonRepository
import com.logoped_plus.ui.screen.schedule.model.LessonUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
            lessons = lessonRepository.getLessons().map { it.toUiModel() }
        )
    )

    val uiState: StateFlow<ScheduleUiState> = _uiState.asStateFlow()

    fun onAction(action: ScheduleAction) {
        when (action) {
            is ScheduleAction.SelectDate -> {
                selectDate(action.date)
            }

            is ScheduleAction.SelectLesson -> {
                selectLesson(action.lessonId)
            }

            ScheduleAction.CloseLesson -> {
                closeLesson()
            }

            ScheduleAction.StartCreatingLesson -> {
                startCreatingLesson()
            }

            ScheduleAction.CancelCreatingLesson -> {
                cancelCreatingLesson()
            }

            is ScheduleAction.CreateLesson -> {
                createLesson(action.lesson)
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
            comment = comment
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
            it.copy(selectedLesson = null)
        }
    }

    private fun startCreatingLesson() {
        _uiState.update {
            it.copy(isCreatingLesson = true)
        }
    }

    private fun cancelCreatingLesson() {
        _uiState.update {
            it.copy(isCreatingLesson = false)
        }
    }

    private fun createLesson(lesson: Lesson) {
        lessonRepository.addLesson(lesson)

        _uiState.update {
            it.copy(
                lessons = lessonRepository.getLessons().map { lesson -> lesson.toUiModel() },
                isCreatingLesson = false
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
