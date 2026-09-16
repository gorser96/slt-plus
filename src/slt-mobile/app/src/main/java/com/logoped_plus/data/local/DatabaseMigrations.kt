package com.logoped_plus.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `lessons` (`position` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `id` TEXT NOT NULL, `scheduledEpochDay` INTEGER NOT NULL, `scheduledNanoOfDay` INTEGER NOT NULL, `durationMinutes` INTEGER NOT NULL, `comment` TEXT NOT NULL)")
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_lessons_id` ON `lessons` (`id`)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `lesson_participants` (`lessonId` TEXT NOT NULL, `childId` TEXT NOT NULL, `ordinal` INTEGER NOT NULL, PRIMARY KEY(`lessonId`, `childId`), FOREIGN KEY(`lessonId`) REFERENCES `lessons`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE, FOREIGN KEY(`childId`) REFERENCES `children`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT)")
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_lesson_participants_lessonId_ordinal` ON `lesson_participants` (`lessonId`, `ordinal`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_lesson_participants_childId` ON `lesson_participants` (`childId`)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `lesson_videos` (`lessonId` TEXT NOT NULL, `uri` TEXT NOT NULL, `ordinal` INTEGER NOT NULL, PRIMARY KEY(`lessonId`, `uri`), FOREIGN KEY(`lessonId`) REFERENCES `lessons`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE)")
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_lesson_videos_lessonId_ordinal` ON `lesson_videos` (`lessonId`, `ordinal`)")
    }
}
