package com.logoped_plus.data.local

import androidx.room.Embedded
import androidx.room.Relation
import com.logoped_plus.domain.model.Lesson
import com.logoped_plus.domain.model.VideoAttachment
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class LessonWithRelations(
    @Embedded val lesson: LessonEntity,
    @Relation(parentColumn = "id", entityColumn = "lessonId") val participants: List<LessonParticipantEntity>,
    @Relation(parentColumn = "id", entityColumn = "lessonId") val videos: List<LessonVideoEntity>
)

fun LessonWithRelations.toDomain() = Lesson(
    id = lesson.id,
    scheduledAt = LocalDateTime.of(LocalDate.ofEpochDay(lesson.scheduledEpochDay), LocalTime.ofNanoOfDay(lesson.scheduledNanoOfDay)),
    durationMinutes = lesson.durationMinutes,
    comment = lesson.comment,
    childIds = participants.sortedBy { it.ordinal }.map { it.childId },
    videoAttachments = videos.sortedBy { it.ordinal }.map { VideoAttachment(it.uri) }
)

fun Lesson.toEntity(position: Long = 0) = LessonEntity(
    position, id, scheduledAt.toLocalDate().toEpochDay(), scheduledAt.toLocalTime().toNanoOfDay(), durationMinutes, comment
)
