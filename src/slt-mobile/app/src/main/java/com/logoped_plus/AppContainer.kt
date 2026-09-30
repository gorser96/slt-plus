package com.logoped_plus

import android.content.Context
import com.logoped_plus.data.local.ChildrenDatabase
import com.logoped_plus.data.media.MediaStoreVideoScanner
import com.logoped_plus.data.preferences.SettingsStore
import com.logoped_plus.data.repository.RoomLessonRepository
import com.logoped_plus.data.repository.RoomChildRepository
import com.logoped_plus.domain.usecase.FindLessonVideosUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

class AppContainer(context: Context, databaseName: String = "children.db") : AutoCloseable {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val database = ChildrenDatabase.create(context, databaseName)
    val childRepository = RoomChildRepository(database.childDao(), scope)
    val lessonRepository = RoomLessonRepository(database.lessonDao(), childRepository, scope)
    val settings = SettingsStore(context)
    val videoLibrary = MediaStoreVideoScanner(context)
    val findLessonVideos = FindLessonVideosUseCase(videoLibrary)

    override fun close() {
        scope.cancel()
        database.close()
    }
}
