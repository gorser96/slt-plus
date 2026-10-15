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
    @Query("DELETE FROM lessons WHERE id = :id")
    abstract suspend fun deleteLesson(id: String)

    @Transaction
    open suspend fun addOnce(lesson: Lesson): LessonWriteResult {
        invalidReason(lesson)?.let { return LessonWriteResult.Failure(it) }
        val liveIds = lesson.participants.filter { it.removedName == null }.map { it.childId }
        if (liveIds.isNotEmpty() && knownChildren(liveIds).toSet() != liveIds.toSet())
            return LessonWriteResult.Failure(LessonWriteResult.Reason.UnknownChild)
        val existing = find(lesson.id)
        if (existing != null) return if (existing.toDomain() == lesson) LessonWriteResult.Success(lesson)
            else LessonWriteResult.Failure(LessonWriteResult.Reason.Conflict)
        insertLesson(lesson.toEntity())
        insertRelations(lesson)
        return LessonWriteResult.Success(lesson)
    }

    protected suspend fun insertRelations(lesson: Lesson) {
        insertParticipants(lesson.participants.mapIndexed { index, p -> LessonParticipantEntity(lesson.id, p.childId, p.removedName, index) })
        insertVideos(lesson.videoAttachments.mapIndexed { index, video -> LessonVideoEntity(lesson.id, video.uri, index) })
    }

    @Transaction
    open suspend fun updateExisting(lesson: Lesson): LessonWriteResult {
        invalidReason(lesson)?.let { return LessonWriteResult.Failure(it) }
        val existing = find(lesson.id) ?: return LessonWriteResult.Failure(LessonWriteResult.Reason.NotFound)
        val liveIds = lesson.participants.filter { it.removedName == null }.map { it.childId }
        if (liveIds.isNotEmpty() && knownChildren(liveIds).toSet() != liveIds.toSet())
            return LessonWriteResult.Failure(LessonWriteResult.Reason.UnknownChild)
        check(updateScalars(lesson.toEntity(existing.lesson.position)) == 1)
        clearParticipants(lesson.id)
        clearVideos(lesson.id)
        insertRelations(lesson)
        return LessonWriteResult.Success(lesson)
    }

    @Transaction
    open suspend fun deleteExisting(id: String): LessonWriteResult {
        val existing = find(id) ?: return LessonWriteResult.Failure(LessonWriteResult.Reason.NotFound)
        deleteLesson(id)
        return LessonWriteResult.Success(existing.toDomain())
    }
}

internal fun invalidReason(lesson: Lesson): LessonWriteResult.Reason? =
    if (lesson.id.isBlank() || lesson.durationMinutes <= 0 || lesson.participants.isEmpty() ||
        lesson.participants.any { it.childId.isBlank() } ||
        lesson.participants.map { it.childId }.distinct().size != lesson.participants.size ||
        lesson.videoAttachments.any { it.uri.isBlank() } ||
        lesson.videoAttachments.map { it.uri }.distinct().size != lesson.videoAttachments.size)
        LessonWriteResult.Reason.InvalidData else null
