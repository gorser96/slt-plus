package com.logoped_plus.domain.repository

import com.logoped_plus.domain.model.Lesson
import java.time.LocalDate

interface LessonRepository {

    fun getLessons(): List<Lesson>

    fun getLessonsByDate(date: LocalDate): List<Lesson>

    fun getLessonById(id: String): Lesson?

    fun addLesson(lesson: Lesson)

    fun updateLesson(lesson: Lesson)
}
