package com.logoped_plus.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Update
import androidx.room.Query
import androidx.room.Transaction
import com.logoped_plus.domain.model.Lesson
import com.logoped_plus.domain.repository.LessonWriteResult
import kotlinx.coroutines.flow.Flow

@Dao
abstract class LessonDao {
    @Transaction
    @Query("SELECT * FROM lessons ORDER BY position ASC")
    abstract fun observeLessons(): Flow<List<LessonWithRelations>>

    @Transaction
    @Query("SELECT * FROM lessons WHERE id = :id")
    abstract suspend fun find(id: String): LessonWithRelations?

    @Query("SELECT id FROM children WHERE id IN (:ids)")
    abstract suspend fun knownChildren(ids: List<String>): List<String>

    @Insert abstract suspend fun insertLesson(lesson: LessonEntity)
    @Update abstract suspend fun updateScalars(lesson: LessonEntity): Int
    @Insert abstract suspend fun insertParticipants(rows: List<LessonParticipantEntity>)
    @Insert abstract suspend fun insertVideos(rows: List<LessonVideoEntity>)
    @Query("DELETE FROM lesson_participants WHERE lessonId = :id")
    abstract suspend fun clearParticipants(id: String)
    @Query("DELETE FROM lesson_videos WHERE lessonId = :id")
    abstract suspend fun clearVideos(id: String)

    @Transaction
    open suspend fun addOnce(lesson: Lesson): LessonWriteResult {
        invalidReason(lesson)?.let { return LessonWriteResult.Failure(it) }
        if (knownChildren(lesson.childIds).toSet() != lesson.childIds.toSet())
            return LessonWriteResult.Failure(LessonWriteResult.Reason.UnknownChild)
        val existing = find(lesson.id)
        if (existing != null) return if (existing.toDomain() == lesson) LessonWriteResult.Success(lesson)
            else LessonWriteResult.Failure(LessonWriteResult.Reason.Conflict)
        insertLesson(lesson.toEntity())
        insertRelations(lesson)
        return LessonWriteResult.Success(lesson)
    }

    protected suspend fun insertRelations(lesson: Lesson) {
        insertParticipants(lesson.childIds.mapIndexed { index, id -> LessonParticipantEntity(lesson.id, id, index) })
        insertVideos(lesson.videoAttachments.mapIndexed { index, video -> LessonVideoEntity(lesson.id, video.uri, index) })
    }

    @Transaction
    open suspend fun updateExisting(lesson: Lesson): LessonWriteResult {
        invalidReason(lesson)?.let { return LessonWriteResult.Failure(it) }
        val existing = find(lesson.id) ?: return LessonWriteResult.Failure(LessonWriteResult.Reason.NotFound)
        if (knownChildren(lesson.childIds).toSet() != lesson.childIds.toSet())
            return LessonWriteResult.Failure(LessonWriteResult.Reason.UnknownChild)
        check(updateScalars(lesson.toEntity(existing.lesson.position)) == 1)
        clearParticipants(lesson.id)
        clearVideos(lesson.id)
        insertRelations(lesson)
        return LessonWriteResult.Success(lesson)
    }
}

internal fun invalidReason(lesson: Lesson): LessonWriteResult.Reason? =
    if (lesson.id.isBlank() || lesson.durationMinutes <= 0 || lesson.childIds.isEmpty() ||
        lesson.childIds.any { it.isBlank() } || lesson.childIds.distinct().size != lesson.childIds.size ||
        lesson.videoAttachments.any { it.uri.isBlank() } ||
        lesson.videoAttachments.map { it.uri }.distinct().size != lesson.videoAttachments.size)
        LessonWriteResult.Reason.InvalidData else null
