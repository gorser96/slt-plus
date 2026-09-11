package com.logoped_plus.ui.screen.children

import com.logoped_plus.domain.model.Child
import com.logoped_plus.domain.repository.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChildrenViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    @Before fun setup() = Dispatchers.setMain(dispatcher)
    @After fun cleanup() = Dispatchers.resetMain()

    @Test fun addsTrimmedNamesAndKeepsNamesakesSeparate() = runTest(dispatcher) {
        val repo = FakeChildren()
        val vm = ChildrenViewModel(repo)
        repeat(2) {
            vm.openNew(); vm.changeName("  Анна  Петрова  "); vm.save(); runCurrent()
            assertNull(vm.uiState.value.editor)
        }
        assertEquals(listOf("Анна  Петрова", "Анна  Петрова"), repo.calls.map { it.name })
        assertNotEquals(repo.calls[0].id, repo.calls[1].id)
    }

    @Test fun blankAndCancelDoNotWrite() = runTest(dispatcher) {
        val repo = FakeChildren(); val vm = ChildrenViewModel(repo)
        vm.openNew(); vm.changeName("   "); vm.save(); runCurrent()
        assertTrue(repo.calls.isEmpty())
        vm.changeName("Анна"); vm.cancel(); runCurrent()
        assertNull(vm.uiState.value.editor); assertTrue(repo.calls.isEmpty())
    }

    @Test fun savingCannotBeRepeatedOrDismissedAndFailureKeepsIdentity() = runTest(dispatcher) {
        val repo = FakeChildren(); repo.gate = CompletableDeferred()
        val vm = ChildrenViewModel(repo)
        vm.openNew(); vm.changeName("Анна"); val id = vm.uiState.value.editor!!.id
        vm.save(); vm.save(); vm.cancel(); vm.changeName("Потеря"); runCurrent()
        assertTrue(vm.uiState.value.editor!!.saving)
        assertEquals("Анна", vm.uiState.value.editor!!.name)
        assertEquals(1, repo.calls.size)
        repo.gate!!.complete(ChildWriteResult.Failure(ChildWriteResult.Reason.StorageUnavailable)); runCurrent()
        assertEquals(id, vm.uiState.value.editor!!.id)
        assertFalse(vm.uiState.value.editor!!.saving)
        repo.gate = null; vm.save(); runCurrent()
        assertEquals(listOf(id, id), repo.calls.map { it.id }); assertNull(vm.uiState.value.editor)
    }

    @Test fun renameUsesSameIdentityAndReadRetryDoesNotWrite() = runTest(dispatcher) {
        val repo = FakeChildren(); val vm = ChildrenViewModel(repo)
        vm.openEdit(Child("id", "Старое")); vm.changeName(" Новое ")
        repo.state.value = ChildLoadState.Error(); runCurrent()
        vm.save(); vm.retryLoading(); runCurrent()
        assertTrue(repo.calls.isEmpty()); assertEquals(1, repo.retries)
        assertEquals(" Новое ", vm.uiState.value.editor!!.name)
        repo.state.value = ChildLoadState.Ready(emptyList()); runCurrent()
        vm.save(); runCurrent()
        assertEquals(Child("id", "Новое"), repo.calls.single()); assertEquals(1, repo.updates)
    }

    private class FakeChildren : ChildRepository {
        override val state = MutableStateFlow<ChildLoadState>(ChildLoadState.Ready(emptyList()))
        val calls = mutableListOf<Child>()
        var gate: CompletableDeferred<ChildWriteResult>? = null
        var retries = 0
        var updates = 0
        override fun retryLoading() { retries++ }
        override suspend fun addChild(child: Child): ChildWriteResult {
            calls += child
            return gate?.await() ?: ChildWriteResult.Success
        }
        override suspend fun updateChild(child: Child): ChildWriteResult { updates++; return addChild(child) }
    }
}
