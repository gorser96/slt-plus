package com.logoped_plus.ui.screen

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.logoped_plus.domain.model.Child
import com.logoped_plus.domain.repository.ChildLoadState
import com.logoped_plus.ui.screen.schedule.*
import com.logoped_plus.ui.screen.schedule.model.LessonUiModel
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth

class ScheduleScreenTest {
    @get:Rule val compose = createComposeRule()

    @Test fun readFailureDisablesSaveWithoutDiscardingCommentDraft() {
        val child = Child("id", "Анна")
        val lesson = LessonUiModel("lesson", LocalDateTime.of(2026, 9, 9, 10, 0), 40, "Анна", "", listOf("id"), emptyList())
        val state = mutableStateOf(ScheduleUiState(
            selectedDate = LocalDate.of(2026, 9, 9), displayedMonth = YearMonth.of(2026, 9),
            displayedWeekStart = LocalDate.of(2026, 9, 7), selectedLesson = lesson,
            children = listOf(child), childLoadState = ChildLoadState.Ready(listOf(child))
        ))
        val actions = mutableListOf<ScheduleAction>()
        compose.setContent { MaterialTheme { ScheduleContent(state.value, actions::add) } }
        compose.onNodeWithText("Комментарий специалиста").performTextInput("Черновик")
        compose.runOnIdle { state.value = state.value.copy(childLoadState = ChildLoadState.Error(listOf(child))) }
        compose.onNodeWithText("Сохранить").performScrollTo().assertIsNotEnabled()
        compose.onNodeWithText("Повторить загрузку").performClick()
        compose.runOnIdle { assertEquals(ScheduleAction.RetryChildren, actions.last()); state.value = state.value.copy(childLoadState = ChildLoadState.Ready(listOf(child))) }
        compose.onNodeWithText("Черновик").assertExists()
        compose.onNodeWithText("Сохранить").performScrollTo().performClick()
        compose.runOnIdle { assertEquals("Черновик", (actions.last() as ScheduleAction.UpdateLesson).comment) }
    }
}
