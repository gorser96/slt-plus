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

    @Transaction
    open suspend fun addOnce(id: String, name: String): Boolean {
        val existing = find(id)
        if (existing != null) return existing.name == name
        insert(ChildEntity(id = id, name = name))
        return true
    }
}
