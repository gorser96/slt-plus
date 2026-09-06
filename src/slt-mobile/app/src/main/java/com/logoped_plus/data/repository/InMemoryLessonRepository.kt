package com.logoped_plus.data.repository

import com.logoped_plus.domain.model.Lesson
import com.logoped_plus.domain.repository.LessonRepository
import java.time.LocalDate
import java.time.LocalDateTime

class InMemoryLessonRepository : LessonRepository {

    private val lessons = mutableListOf(
        Lesson(
            childIds = listOf("child-1"),
            scheduledAt = LocalDateTime.of(2026, 9, 6, 10, 0),
            durationMinutes = 45,
            comment = "Постановка звука Р"
        ),
        Lesson(
            childIds = listOf("child-2"),
            scheduledAt = LocalDateTime.of(2026, 9, 6, 12, 30),
            durationMinutes = 60,
            comment = "Автоматизация звука Ш"
        ),
        Lesson(
            childIds = listOf("child-1", "child-3"),
            scheduledAt = LocalDateTime.of(2026, 9, 7, 15, 0),
            durationMinutes = 45,
            comment = "Групповое занятие"
        )
    )

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
}