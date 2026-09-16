# com.logoped_plus.data.local

Общая Room БД детей и занятий. Room-типы остаются в data. [Общий указатель](../../../../../../../../PACKAGES.md).

| Файл | Назначение |
|---|---|
| [ChildDao.kt](ChildDao.kt) | Наблюдение по position, вставка по UUID, переименование |
| [ChildEntity.kt](ChildEntity.kt) | UUID, имя, независимый порядок добавления |
| [ChildrenDatabase.kt](ChildrenDatabase.kt) | Room v2, children.db, MIGRATION_1_2; без seed/destructive fallback |
| [DatabaseMigrations.kt](DatabaseMigrations.kt) | Добавочная миграция 1→2 без пересоздания children |
| [LessonEntity.kt](LessonEntity.kt) | UUID, position, epochDay/nanoOfDay, длительность и комментарий |
| [LessonParticipantEntity.kt](LessonParticipantEntity.kt) | Уникальные упорядоченные childId; FK children RESTRICT, lessons CASCADE |
| [LessonVideoEntity.kt](LessonVideoEntity.kt) | Уникальные URI с ordinal; FK lessons CASCADE |
| [LessonWithRelations.kt](LessonWithRelations.kt) | Целый агрегат, преобразования Lesson и сортировка связей по ordinal |
| [LessonDao.kt](LessonDao.kt) | Транзакционное наблюдение; addOnce по UUID+агрегату, updateExisting без смены position; проверка детей и атомарная запись связей |

Потребитель — [репозитории](../repository/PACKAGE.md). Время не переводится в UTC. REPLACE родителя не используется. Тестовый LegacyChildrenDatabase воспроизводит SQL v1 из исходной ревизии: пользователь удалил экспорт 1.json; MigrationTestHelper валидирует экспорт v2.

## Проверки

- [LegacyChildrenDatabase.kt](../../../../../../androidTest/java/com/logoped_plus/data/local/LegacyChildrenDatabase.kt) — androidTest.
- [LessonMigrationTest.kt](../../../../../../androidTest/java/com/logoped_plus/data/local/LessonMigrationTest.kt) — androidTest.

Результаты и открытые критерии — [приёмка 010](../../../../../../../../openspec/verification/persist-lessons/validation.md).
