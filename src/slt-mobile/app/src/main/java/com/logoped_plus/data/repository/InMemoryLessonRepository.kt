package com.logoped_plus.data.repository

import com.logoped_plus.domain.model.Lesson
import com.logoped_plus.domain.repository.LessonRepository
import java.time.LocalDate


class InMemoryLessonRepository : LessonRepository {

    private val lessons = mutableListOf<Lesson>()

    override fun getLessons(): List<Lesson> {
        return lessons.toList()
    }

    override fun getLessonsByDate(date: LocalDate): List<Lesson> {
        return lessons
            .filter { it.scheduledAt.toLocalDate() == date }
            .sortedBy { it.scheduledAt }
    }

    override fun getLessonById(id: String): Lesson? {
        return lessons.find { it.id == id }
    }

    override fun addLesson(lesson: Lesson) {
        lessons.add(lesson)
    }

    override fun updateLesson(lesson: Lesson) {
        val index = lessons.indexOfFirst { it.id == lesson.id }
        require(index >= 0) { "Lesson not found" }
        lessons[index] = lesson
    }
}
