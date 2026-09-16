package com.logoped_plus.ui.screen.schedule

import com.logoped_plus.domain.model.Lesson
import com.logoped_plus.domain.model.VideoAttachment
import com.logoped_plus.domain.repository.LessonWriteResult
import java.time.DateTimeException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

enum class LessonEditorMode { CREATE, DETAILS, EDIT }
enum class LessonTextField { HOUR, MINUTE, DURATION, COMMENT }
sealed interface LessonEditorStatus {
    data object Idle : LessonEditorStatus
    data object Saving : LessonEditorStatus
    data class Failure(val reason: LessonWriteResult.Reason) : LessonEditorStatus
}

data class LessonEditorState(
    val sessionId: String = UUID.randomUUID().toString(),
    val lessonId: String = UUID.randomUUID().toString(),
    val mode: LessonEditorMode,
    val original: Lesson? = null,
    val epochDay: Long,
    val hour: String,
    val minute: String,
    val duration: String = "40",
    val childIds: List<String> = emptyList(),
    val comment: String = "",
    val videoUris: List<String> = emptyList(),
    val status: LessonEditorStatus = LessonEditorStatus.Idle
) {
    val saving: Boolean get() = status is LessonEditorStatus.Saving
    val changed: Boolean get() = toLesson() != original

    fun toLesson(): Lesson? {
        val h = hour.toIntOrNull()?.takeIf { it in 0..23 } ?: return null
        val m = minute.toIntOrNull()?.takeIf { it in 0..59 } ?: return null
        val minutes = duration.toIntOrNull()?.takeIf { it > 0 } ?: return null
        if (childIds.isEmpty() || childIds.distinct().size != childIds.size) return null
        val dateTime = try {
            // Material-only edits must retain seconds/nanoseconds from the original.
            val date = LocalDate.ofEpochDay(epochDay)
            val old = original?.scheduledAt
            if (old != null && old.toLocalDate() == date && old.hour == h && old.minute == m) old
            else LocalDateTime.of(date, LocalTime.of(h, m))
        } catch (_: DateTimeException) { return null }
        return Lesson(lessonId, childIds.toList(), dateTime, minutes, comment, videoUris.map(::VideoAttachment))
    }

    companion object {
        fun from(lesson: Lesson, mode: LessonEditorMode = LessonEditorMode.DETAILS) = LessonEditorState(
            lessonId = lesson.id, mode = mode, original = lesson,
            epochDay = lesson.scheduledAt.toLocalDate().toEpochDay(),
            hour = lesson.scheduledAt.hour.toString().padStart(2, '0'),
            minute = lesson.scheduledAt.minute.toString().padStart(2, '0'),
            duration = lesson.durationMinutes.toString(), childIds = lesson.childIds.toList(),
            comment = lesson.comment, videoUris = lesson.videoAttachments.map { it.uri }
        )
    }
}
