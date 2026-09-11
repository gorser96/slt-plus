package com.logoped_plus.data.repository

import com.logoped_plus.domain.model.VideoAttachment
import com.logoped_plus.domain.model.Lesson
import java.time.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Test

class InMemoryLessonRepositoryTest {
    @Test
    fun startsEmpty() {
        assertEquals(emptyList<Lesson>(), InMemoryLessonRepository().getLessons())
    }
    @Test
    fun updateReplacesExistingLessonAndAllowsClearingAttachments() {
        val repository = InMemoryLessonRepository()
        repository.addLesson(Lesson(childIds = listOf("test-1"), scheduledAt = LocalDateTime.of(2026, 9, 9, 10, 0), durationMinutes = 40))
        repository.addLesson(Lesson(childIds = listOf("test-2"), scheduledAt = LocalDateTime.of(2026, 9, 9, 12, 0), durationMinutes = 40))
        val before = repository.getLessons()
        val original = before.first()
        val edited = original.copy(
            scheduledAt = original.scheduledAt.plusDays(1),
            durationMinutes = 30,
            comment = "Результаты занятия",
            videoAttachments = listOf(VideoAttachment("content://videos/1"))
        )

        repository.updateLesson(edited)

        assertEquals(before.size, repository.getLessons().size)
        assertEquals(edited, repository.getLessonById(original.id))
        assertEquals(before.drop(1), repository.getLessons().drop(1))
        assertEquals(false, repository.getLessonsByDate(original.scheduledAt.toLocalDate())
            .any { it.id == original.id })

        val cleared = edited.copy(comment = "", videoAttachments = emptyList())
        repository.updateLesson(cleared)
        assertEquals(cleared, repository.getLessonById(original.id))
    }
}
