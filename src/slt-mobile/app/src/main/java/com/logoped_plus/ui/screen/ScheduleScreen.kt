package com.logoped_plus.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.logoped_plus.ui.screen.schedule.LessonCreateView
import com.logoped_plus.ui.screen.schedule.LessonDetailsView
import com.logoped_plus.ui.screen.schedule.LessonEditView
import com.logoped_plus.ui.screen.schedule.MonthScheduleView
import com.logoped_plus.ui.screen.schedule.ScheduleAction
import com.logoped_plus.ui.screen.schedule.ScheduleUiState
import com.logoped_plus.ui.screen.schedule.ScheduleViewMode
import com.logoped_plus.ui.screen.schedule.ScheduleViewModel
import com.logoped_plus.ui.screen.schedule.WeekScheduleView

@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    ScheduleContent(
        uiState = uiState,
        onAction = viewModel::onAction
    )
}

@Composable
private fun ScheduleContent(
    uiState: ScheduleUiState,
    onAction: (ScheduleAction) -> Unit
) {
    val scrollState = rememberScrollState()
    val selectedLesson = uiState.selectedLesson
    var commentDraft by rememberSaveable(selectedLesson?.id, selectedLesson?.comment) {
        mutableStateOf(selectedLesson?.comment.orEmpty())
    }
    var videoDraft by rememberSaveable(selectedLesson?.id, selectedLesson?.videoUris) {
        mutableStateOf(selectedLesson?.videoUris.orEmpty())
    }

    BackHandler(
        enabled = selectedLesson != null || uiState.isCreatingLesson
    ) {
        when {
            uiState.isEditingLesson -> {
                onAction(ScheduleAction.CancelEditingLesson)
            }

            selectedLesson != null -> {
                onAction(ScheduleAction.CloseLesson)
            }

            uiState.isCreatingLesson -> {
                onAction(ScheduleAction.CancelCreatingLesson)
            }
        }
    }

    if (selectedLesson != null && uiState.isEditingLesson) {
        LessonEditView(
            lesson = selectedLesson.copy(comment = commentDraft, videoUris = videoDraft),
            children = uiState.children,
            onBack = { onAction(ScheduleAction.CancelEditingLesson) },
            onSave = { scheduledAt, childIds, durationMinutes, comment, videoUris ->
                commentDraft = comment
                videoDraft = videoUris
                onAction(ScheduleAction.UpdateLesson(
                    selectedLesson.id, scheduledAt, childIds, durationMinutes, comment, videoUris
                ))
            }
        )
        return
    }

    if (selectedLesson != null) {
        LessonDetailsView(
            uiModel = selectedLesson,
            comment = commentDraft,
            videoUris = videoDraft,
            onCommentChange = { commentDraft = it },
            onVideosChange = { videoDraft = it },
            onSave = {
                onAction(ScheduleAction.UpdateLesson(
                    selectedLesson.id, selectedLesson.scheduledAt, selectedLesson.childIds,
                    selectedLesson.durationMinutes, commentDraft, videoDraft
                ))
            },
            onEdit = { onAction(ScheduleAction.StartEditingLesson) },
            onBack = {
                onAction(ScheduleAction.CloseLesson)
            }
        )

        return
    }

    if (uiState.isCreatingLesson) {
        LessonCreateView(
            initialDate = uiState.creationDateTime ?: uiState.selectedDate.atStartOfDay(),
            onBack = {
                onAction(ScheduleAction.CancelCreatingLesson)
            },
            onCreate = { scheduledAt, childIds, durationMinutes ->
                onAction(
                    ScheduleAction.CreateLesson(
                        scheduledAt = scheduledAt,
                        childIds = childIds,
                        durationMinutes = durationMinutes
                    )
                )
            },
            children = uiState.children
        )

        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .then(
                if (uiState.viewMode == ScheduleViewMode.MONTH) Modifier.verticalScroll(scrollState)
                else Modifier
            )
            .padding(16.dp)
    ) {
        ScheduleViewModeSelector(
            selectedMode = uiState.viewMode,
            onModeSelected = { mode ->
                onAction(
                    ScheduleAction.ChangeViewMode(mode)
                )
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        when (uiState.viewMode) {
            ScheduleViewMode.MONTH -> {
                MonthScheduleView(
                    displayedMonth = uiState.displayedMonth,
                    selectedDate = uiState.selectedDate,
                    lessons = uiState.lessons,
                    onDateSelected = { date ->
                        onAction(
                            ScheduleAction.SelectDate(date)
                        )
                    },
                    onPreviousMonth = {
                        onAction(ScheduleAction.PreviousMonth)
                    },
                    onNextMonth = {
                        onAction(ScheduleAction.NextMonth)
                    },
                    onLessonClick = { lesson ->
                        onAction(
                            ScheduleAction.SelectLesson(lesson.id)
                        )
                    }
                )
            }

            ScheduleViewMode.WEEK -> {
                WeekScheduleView(
                    onEmptyHourDoubleClick = { dateTime ->
                        onAction(ScheduleAction.StartCreatingLesson(dateTime))
                    },
                    modifier = Modifier.weight(1f),
                    weekStart = uiState.displayedWeekStart,
                    lessons = uiState.lessons,
                    onPreviousWeek = {
                        onAction(ScheduleAction.PreviousWeek)
                    },
                    onNextWeek = {
                        onAction(ScheduleAction.NextWeek)
                    },
                    onLessonClick = { uiModel ->
                        onAction(
                            ScheduleAction.SelectLesson(uiModel.id)
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                onAction(ScheduleAction.StartCreatingLesson())
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Добавить занятие")
        }
    }
}

@Composable
private fun ScheduleViewModeSelector(
    selectedMode: ScheduleViewMode, onModeSelected: (ScheduleViewMode) -> Unit
) {
    SingleChoiceSegmentedButtonRow(
        modifier = Modifier.fillMaxWidth()
    ) {
        SegmentedButton(
            selected = selectedMode == ScheduleViewMode.MONTH, onClick = {
                onModeSelected(ScheduleViewMode.MONTH)
            }, shape = SegmentedButtonDefaults.itemShape(
                index = 0, count = 2
            )
        ) {
            Text("Месяц")
        }

        SegmentedButton(
            selected = selectedMode == ScheduleViewMode.WEEK, onClick = {
                onModeSelected(ScheduleViewMode.WEEK)
            }, shape = SegmentedButtonDefaults.itemShape(
                index = 1, count = 2
            )
        ) {
            Text("Неделя")
        }
    }
}
