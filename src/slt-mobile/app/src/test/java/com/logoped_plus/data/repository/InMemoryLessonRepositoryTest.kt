package com.logoped_plus.data.repository

import com.logoped_plus.domain.model.VideoAttachment
import org.junit.Assert.assertEquals
import org.junit.Test

class InMemoryLessonRepositoryTest {
    @Test
    fun updateReplacesExistingLessonAndAllowsClearingAttachments() {
        val repository = InMemoryLessonRepository()
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
