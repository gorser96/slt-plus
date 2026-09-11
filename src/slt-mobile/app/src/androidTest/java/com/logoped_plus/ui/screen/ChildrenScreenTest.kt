package com.logoped_plus.ui.screen

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.logoped_plus.domain.model.Child
import com.logoped_plus.domain.repository.*
import com.logoped_plus.ui.screen.children.ChildrenViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class ChildrenScreenTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()

    @Test fun emptyLoadingAndErrorAreDifferentAndDialogCanRetryReading() {
        val repo = FakeChildren(); lateinit var vm: ChildrenViewModel
        compose.runOnIdle { vm = ChildrenViewModel(repo) }
        compose.setContent { MaterialTheme { ChildrenScreen(vm) } }
        compose.onNodeWithText("Детей пока нет").assertExists()
        compose.onNodeWithContentDescription("Добавить ребёнка").performClick()
        compose.onNodeWithText("Имя").performTextInput("Анна")
        compose.runOnIdle { repo.state.value = ChildLoadState.Error() }
        compose.onAllNodesWithText("Повторить загрузку").onLast().performClick()
        compose.runOnIdle {
            assertEquals(1, repo.retries); assertEquals(0, repo.writes)
            assertEquals("Анна", vm.uiState.value.editor!!.name)
        }
        compose.onNodeWithText("Добавить").assertIsNotEnabled()
        compose.runOnIdle { repo.state.value = ChildLoadState.Ready(emptyList()) }
        compose.onNodeWithText("Добавить").performClick()
        compose.onNodeWithText("Сохранение…").assertExists()
        compose.onNodeWithText("Отмена").assertIsNotEnabled()
        compose.onNodeWithText("Добавить").assertIsNotEnabled()
        compose.runOnIdle { repo.result.complete(ChildWriteResult.Failure(ChildWriteResult.Reason.StorageUnavailable)) }
        compose.onNodeWithText("Не удалось сохранить ребёнка. Повторите попытку").assertExists()
        compose.onNodeWithText("Анна").assertExists()
    }

    @Test fun recreationAndBackKeepPendingEditorWithoutAnotherWrite() {
        val repo = FakeChildren()
        val factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = ChildrenViewModel(repo) as T
        }
        lateinit var original: ChildrenViewModel
        compose.activityRule.scenario.onActivity { activity ->
            original = ViewModelProvider(activity, factory)[ChildrenViewModel::class.java]
            activity.setContent { MaterialTheme { ChildrenScreen(original) } }
        }
        compose.onNodeWithContentDescription("Добавить ребёнка").performClick()
        compose.onNodeWithText("Имя").performTextInput("Анна")
        compose.onNodeWithText("Добавить").performClick()
        compose.onNodeWithText("Сохранение…").assertExists()
        androidx.test.espresso.Espresso.pressBack()
        compose.onNodeWithText("Сохранение…").assertExists()
        compose.activityRule.scenario.recreate()
        compose.activityRule.scenario.onActivity { activity ->
            val restored = ViewModelProvider(activity, factory)[ChildrenViewModel::class.java]
            assertSame(original, restored)
            activity.setContent { MaterialTheme { ChildrenScreen(restored) } }
        }
        compose.onNodeWithText("Анна").assertExists()
        compose.onNodeWithText("Отмена").assertIsNotEnabled()
        compose.runOnIdle { assertEquals(1, repo.writes); repo.result.complete(ChildWriteResult.Success) }
        compose.onNodeWithText("Новый ребёнок").assertDoesNotExist()
    }

    private class FakeChildren : ChildRepository {
        override val state = MutableStateFlow<ChildLoadState>(ChildLoadState.Ready(emptyList()))
        val result = CompletableDeferred<ChildWriteResult>()
        var writes = 0
        var retries = 0
        override fun retryLoading() { retries++; state.value = ChildLoadState.Loading() }
        override suspend fun addChild(child: Child): ChildWriteResult { writes++; return result.await() }
        override suspend fun updateChild(child: Child) = addChild(child)
    }
}
