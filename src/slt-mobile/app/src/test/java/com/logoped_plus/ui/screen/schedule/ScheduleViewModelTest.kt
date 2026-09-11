package com.logoped_plus.ui.screen.schedule

import com.logoped_plus.data.repository.InMemoryLessonRepository
import com.logoped_plus.domain.model.Child
import com.logoped_plus.domain.model.Lesson
import com.logoped_plus.domain.repository.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class ScheduleViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    @Before fun setup() = Dispatchers.setMain(dispatcher)
    @After fun cleanup() = Dispatchers.resetMain()

    @Test fun renameRefreshesSelectedLessonWithoutChangingParticipants() = runTest(dispatcher) {
        val children = FakeChildren()
        children.state.value = ChildLoadState.Ready(listOf(Child("id", "Старое")))
        val lessons = InMemoryLessonRepository()
        val lesson = Lesson(childIds = listOf("id"), scheduledAt = LocalDateTime.of(2026, 9, 9, 10, 0), durationMinutes = 30)
        lessons.addLesson(lesson)
        val vm = ScheduleViewModel(lessons, children)
        vm.onAction(ScheduleAction.SelectLesson(lesson.id)); runCurrent()
        children.state.value = ChildLoadState.Ready(listOf(Child("id", "Новое"))); runCurrent()
        assertEquals("Новое", vm.uiState.value.selectedLesson!!.childNames)
        assertEquals(listOf("id"), vm.uiState.value.selectedLesson!!.childIds)
        assertEquals(lesson, lessons.getLessonById(lesson.id))
    }

    @Test fun loadingAndUnknownChildrenCannotCreateOrUpdateLessons() = runTest(dispatcher) {
        val children = FakeChildren(); val lessons = InMemoryLessonRepository()
        val vm = ScheduleViewModel(lessons, children)
        val time = LocalDateTime.of(2026, 9, 9, 10, 0)
        val initialCount = lessons.getLessons().size
        for (load in listOf(ChildLoadState.Loading(), ChildLoadState.Error(), ChildLoadState.Ready(emptyList()))) {
            children.state.value = load
            vm.onAction(ScheduleAction.CreateLesson(time, listOf("id"), 30)); runCurrent()
            assertEquals(initialCount, lessons.getLessons().size)
        }
        children.state.value = ChildLoadState.Ready(listOf(Child("id", "Имя"))); runCurrent()
        vm.onAction(ScheduleAction.CreateLesson(time, listOf("id"), 30)); runCurrent()
        val created = lessons.getLessons().last()
        children.state.value = ChildLoadState.Error(children.state.value.children)
        vm.onAction(ScheduleAction.UpdateLesson(created.id, time, listOf("id"), 60, "Изменение", emptyList()))
        assertEquals(created, lessons.getLessonById(created.id))
    }

    private class FakeChildren : ChildRepository {
        override val state = MutableStateFlow<ChildLoadState>(ChildLoadState.Loading())
        override fun retryLoading() = Unit
        override suspend fun addChild(child: Child) = ChildWriteResult.Success
        override suspend fun updateChild(child: Child) = ChildWriteResult.Success
    }
}
