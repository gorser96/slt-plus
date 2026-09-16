package com.logoped_plus.domain.repository

import com.logoped_plus.domain.model.Lesson
import kotlinx.coroutines.flow.StateFlow

interface LessonRepository {

    val state: StateFlow<LessonLoadState>
    fun retryLoading()
    suspend fun addLesson(lesson: Lesson): LessonWriteResult
    suspend fun updateLesson(lesson: Lesson): LessonWriteResult
}
