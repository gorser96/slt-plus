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

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `lesson_participants` RENAME TO `lesson_participants_old`")
        // Table rename does not rename its indexes; drop them so the new table can use the names.
        db.execSQL("DROP INDEX `index_lesson_participants_lessonId_ordinal`")
        db.execSQL("DROP INDEX `index_lesson_participants_childId`")
        db.execSQL("CREATE TABLE `lesson_participants` (`lessonId` TEXT NOT NULL, `childId` TEXT NOT NULL, `childName` TEXT, `ordinal` INTEGER NOT NULL, PRIMARY KEY(`lessonId`, `childId`), FOREIGN KEY(`lessonId`) REFERENCES `lessons`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE)")
        db.execSQL("CREATE UNIQUE INDEX `index_lesson_participants_lessonId_ordinal` ON `lesson_participants` (`lessonId`, `ordinal`)")
        db.execSQL("CREATE INDEX `index_lesson_participants_childId` ON `lesson_participants` (`childId`)")
        db.execSQL("INSERT INTO `lesson_participants` (`lessonId`, `childId`, `childName`, `ordinal`) SELECT `lessonId`, `childId`, NULL, `ordinal` FROM `lesson_participants_old`")
        db.execSQL("DROP TABLE `lesson_participants_old`")
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `tags` (`position` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `id` TEXT NOT NULL, `name` TEXT NOT NULL, `nameKey` TEXT NOT NULL, `colorKey` TEXT NOT NULL)")
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_tags_id` ON `tags` (`id`)")
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_tags_nameKey` ON `tags` (`nameKey`)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `child_tags` (`childId` TEXT NOT NULL, `tagId` TEXT NOT NULL, PRIMARY KEY(`childId`, `tagId`), FOREIGN KEY(`childId`) REFERENCES `children`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE, FOREIGN KEY(`tagId`) REFERENCES `tags`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `child_schedule` (`childId` TEXT NOT NULL, `endEpochDay` INTEGER NOT NULL, PRIMARY KEY(`childId`), FOREIGN KEY(`childId`) REFERENCES `children`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `child_schedule_slots` (`childId` TEXT NOT NULL, `id` TEXT NOT NULL, `dayOfWeek` INTEGER NOT NULL, `startNanoOfDay` INTEGER NOT NULL, `durationMinutes` INTEGER NOT NULL, `ordinal` INTEGER NOT NULL, PRIMARY KEY(`childId`, `id`), FOREIGN KEY(`childId`) REFERENCES `children`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE, FOREIGN KEY(`childId`) REFERENCES `child_schedule`(`childId`) ON UPDATE NO ACTION ON DELETE CASCADE)")
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_child_schedule_slots_childId_ordinal` ON `child_schedule_slots` (`childId`, `ordinal`)")
    }
}
