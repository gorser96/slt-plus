package com.logoped_plus.data.repository

import androidx.test.platform.app.InstrumentationRegistry
import com.logoped_plus.data.local.ChildrenDatabase
import com.logoped_plus.domain.model.Child
import com.logoped_plus.domain.repository.ChildLoadState
import com.logoped_plus.domain.repository.ChildWriteResult
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.junit.Assert.*
import org.junit.Test
import java.util.UUID

class RoomChildRepositoryTest {
    @Test fun fileReopensWithNamesakesOrderAndRename() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val name = "children-test-${UUID.randomUUID()}.db"
        var db = ChildrenDatabase.create(context, name)
        var scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        try {
            var repository = RoomChildRepository(db.childDao(), scope)
            withTimeout(5000) { repository.state.first { it is ChildLoadState.Ready } }
            assertTrue(repository.state.value.children.isEmpty())
            val expected = (0 until 20).map { Child("test-$it", if (it < 2) "Анна" else "Ребёнок $it") }.toMutableList()
            expected.forEach { assertEquals(ChildWriteResult.Success, repository.addChild(it.copy(name = " ${it.name} "))) }
            assertEquals(ChildWriteResult.Success, repository.addChild(expected[0]))
            assertEquals(ChildWriteResult.Failure(ChildWriteResult.Reason.Conflict), repository.addChild(expected[0].copy(name = "Другое")))
            expected[0] = expected[0].copy(name = "Новое имя")
            assertEquals(ChildWriteResult.Success, repository.updateChild(expected[0]))
            assertEquals(ChildWriteResult.Failure(ChildWriteResult.Reason.NotFound), repository.updateChild(Child("missing", "Имя")))
            scope.cancel(); db.close()
            db = ChildrenDatabase.create(context, name)
            scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
            repository = RoomChildRepository(db.childDao(), scope)
            val restored = withTimeout(5000) { repository.state.first { it is ChildLoadState.Ready } }
            assertEquals(expected, restored.children)
        } finally {
            scope.cancel(); db.close(); context.deleteDatabase(name)
        }
    }
}
