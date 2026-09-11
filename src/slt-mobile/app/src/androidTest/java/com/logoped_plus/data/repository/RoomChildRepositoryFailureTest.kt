package com.logoped_plus.data.repository

import android.database.sqlite.SQLiteException
import com.logoped_plus.data.local.ChildDao
import com.logoped_plus.data.local.ChildEntity
import com.logoped_plus.domain.model.Child
import com.logoped_plus.domain.repository.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RoomChildRepositoryFailureTest {
    @Test fun readFailureCanRetryWithoutWritingOrDuplicatingSubscriptions() = runTest {
        val dao = ControlledDao(); dao.failRead = true
        val repository = RoomChildRepository(dao, backgroundScope)
        runCurrent()
        assertTrue(repository.state.value is ChildLoadState.Error)
        dao.failRead = false
        repository.retryLoading(); repository.retryLoading(); runCurrent()
        assertTrue(repository.state.value is ChildLoadState.Ready)
        assertEquals(2, dao.subscriptions)
        assertEquals(0, dao.writes)
    }

    @Test fun failedWriteKeepsSnapshotAndCommittedWriteStaysSuccessfulOnReadFailure() = runTest {
        val dao = ControlledDao(); val repository = RoomChildRepository(dao, backgroundScope)
        runCurrent(); dao.failWrite = true
        assertEquals(ChildWriteResult.Failure(ChildWriteResult.Reason.StorageUnavailable), repository.addChild(Child("new", "Имя")))
        assertEquals(listOf(Child("old", "Прежнее")), repository.state.value.children)
        dao.failWrite = false
        assertEquals(ChildWriteResult.Success, repository.addChild(Child("new", "Имя")))
        dao.rows.value = null; runCurrent()
        assertTrue(repository.state.value is ChildLoadState.Error)
        assertEquals(1, dao.writes)
    }

    @Test fun cancellationPropagatesInsteadOfBecomingStorageFailure() = runTest {
        val dao = ControlledDao(); val repository = RoomChildRepository(dao, backgroundScope)
        runCurrent(); dao.cancelWrite = true
        try {
            repository.addChild(Child("new", "Имя"))
            fail("Cancellation must propagate")
        } catch (_: CancellationException) { }
    }

    private class ControlledDao : ChildDao() {
        val rows = MutableStateFlow<List<ChildEntity>?>(listOf(ChildEntity(1, "old", "Прежнее")))
        var failRead = false
        var failWrite = false
        var cancelWrite = false
        var subscriptions = 0
        var writes = 0
        override fun observeChildren(): Flow<List<ChildEntity>> = flow {
            subscriptions++
            if (failRead) throw SQLiteException("read")
            rows.collect { emit(it ?: throw SQLiteException("read")) }
        }
        override suspend fun find(id: String) = rows.value?.find { it.id == id }
        override suspend fun insert(child: ChildEntity) {
            if (cancelWrite) throw CancellationException()
            if (failWrite) throw SQLiteException("write")
            writes++
        }
        override suspend fun rename(id: String, name: String): Int { insert(ChildEntity(id = id, name = name)); return 1 }
    }
}
