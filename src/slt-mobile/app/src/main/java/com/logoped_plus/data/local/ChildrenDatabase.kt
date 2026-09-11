package com.logoped_plus.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ChildEntity::class], version = 1, exportSchema = true)
abstract class ChildrenDatabase : RoomDatabase() {
    abstract fun childDao(): ChildDao

    companion object {
        fun create(context: Context, name: String = "children.db"): ChildrenDatabase =
            Room.databaseBuilder(context.applicationContext, ChildrenDatabase::class.java, name).build()
    }
}
