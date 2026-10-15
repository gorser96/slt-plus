package com.logoped_plus.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ChildDao {
    @Query("SELECT * FROM children ORDER BY position ASC")
    abstract fun observeChildren(): Flow<List<ChildEntity>>

    @Query("SELECT * FROM children WHERE id = :id")
    abstract suspend fun find(id: String): ChildEntity?

    @Insert
    abstract suspend fun insert(child: ChildEntity)

    @Query("UPDATE children SET name = :name WHERE id = :id")
    abstract suspend fun rename(id: String, name: String): Int

    @Query("SELECT name FROM children WHERE id = :id")
    abstract suspend fun findName(id: String): String?

    @Query("UPDATE lesson_participants SET childName = :name WHERE childId = :id AND childName IS NULL")
    abstract suspend fun snapshotChildName(id: String, name: String)

    @Query("DELETE FROM children WHERE id = :id")
    abstract suspend fun deleteChild(id: String): Int

    @Transaction
    open suspend fun addOnce(id: String, name: String): Boolean {
        val existing = find(id)
        if (existing != null) return existing.name == name
        insert(ChildEntity(id = id, name = name))
        return true
    }

    @Transaction
    open suspend fun deleteOnce(id: String): Boolean {
        val name = findName(id) ?: return false
        snapshotChildName(id, name)
        return deleteChild(id) == 1
    }
}
