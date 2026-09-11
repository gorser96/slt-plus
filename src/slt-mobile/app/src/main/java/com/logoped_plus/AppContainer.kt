package com.logoped_plus

import android.content.Context
import com.logoped_plus.data.local.ChildrenDatabase
import com.logoped_plus.data.repository.InMemoryLessonRepository
import com.logoped_plus.data.repository.RoomChildRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

class AppContainer(context: Context, databaseName: String = "children.db") : AutoCloseable {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val database = ChildrenDatabase.create(context, databaseName)
    val childRepository = RoomChildRepository(database.childDao(), scope)
    val lessonRepository = InMemoryLessonRepository()

    override fun close() {
        scope.cancel()
        database.close()
    }
}
