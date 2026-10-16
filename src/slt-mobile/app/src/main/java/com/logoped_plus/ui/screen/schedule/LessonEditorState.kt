package com.logoped_plus.ui.screen.schedule

import com.logoped_plus.domain.model.Lesson
import com.logoped_plus.domain.model.LessonParticipant
import com.logoped_plus.domain.model.VideoAttachment
import com.logoped_plus.domain.repository.LessonWriteResult
import com.logoped_plus.ui.components.parseTimeText
import java.time.DateTimeException
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

enum class LessonEditorMode { CREATE, DETAILS, EDIT }
enum class LessonTextField { TIME, DURATION, COMMENT }
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
    val time: String,
    val duration: String = "40",
    val participants: List<LessonParticipant> = emptyList(),
    val comment: String = "",
    val videoUris: List<String> = emptyList(),
    val status: LessonEditorStatus = LessonEditorStatus.Idle
) {
    val saving: Boolean get() = status is LessonEditorStatus.Saving
    val changed: Boolean get() = toLesson() != original

    fun toLesson(): Lesson? {
        val timeOfDay = parseTimeText(time) ?: return null
        val minutes = duration.toIntOrNull()?.takeIf { it > 0 } ?: return null
        if (participants.isEmpty() || participants.map { it.childId }.distinct().size != participants.size) return null
        val dateTime = try {
            // Material-only edits must retain seconds/nanoseconds from the original.
            val date = LocalDate.ofEpochDay(epochDay)
            val old = original?.scheduledAt
            if (old != null && old.toLocalDate() == date && old.hour == timeOfDay.hour && old.minute == timeOfDay.minute) old
            else LocalDateTime.of(date, timeOfDay)
        } catch (_: DateTimeException) { return null }
        return Lesson(lessonId, participants.toList(), dateTime, minutes, comment, videoUris.map(::VideoAttachment))
    }

    companion object {
        fun from(lesson: Lesson, mode: LessonEditorMode = LessonEditorMode.DETAILS) = LessonEditorState(
            lessonId = lesson.id, mode = mode, original = lesson,
            epochDay = lesson.scheduledAt.toLocalDate().toEpochDay(),
            time = "%02d:%02d".format(lesson.scheduledAt.hour, lesson.scheduledAt.minute),
            duration = lesson.durationMinutes.toString(), participants = lesson.participants.toList(),
            comment = lesson.comment, videoUris = lesson.videoAttachments.map { it.uri }
        )
    }
}
