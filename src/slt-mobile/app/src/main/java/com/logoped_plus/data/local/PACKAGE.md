# com.logoped_plus.data.local

Общая Room БД детей и занятий. Room-типы остаются в data. [Общий указатель](../../../../../../../../PACKAGES.md).

| Файл | Назначение |
|---|---|
| [ChildDao.kt](ChildDao.kt) | Наблюдение по position, вставка по UUID, переименование; транзакционное удаление deleteOnce (снимок имени участника + удаление, нет имени → false) |
| [ChildEntity.kt](ChildEntity.kt) | UUID, имя, независимый порядок добавления |
| [ChildrenDatabase.kt](ChildrenDatabase.kt) | Room v3, children.db, MIGRATION_1_2 + MIGRATION_2_3; без seed/destructive fallback |
| [DatabaseMigrations.kt](DatabaseMigrations.kt) | Миграции 1→2 (без пересоздания children) и 2→3 (пересоздание lesson_participants: поле childName, без FK на children, данные сохраняются) |
| [LessonEntity.kt](LessonEntity.kt) | UUID, position, epochDay/nanoOfDay, длительность и комментарий |
| [LessonParticipantEntity.kt](LessonParticipantEntity.kt) | Уникальные упорядоченные childId; childName — NULL у актуального участника, имя на момент удаления — у исторического; FK lessons CASCADE, на children нет |
| [LessonVideoEntity.kt](LessonVideoEntity.kt) | Уникальные URI с ordinal; FK lessons CASCADE |
| [LessonWithRelations.kt](LessonWithRelations.kt) | Целый агрегат, преобразования в Lesson (участники — LessonParticipant с childName) и сортировка связей по ordinal |
| [LessonDao.kt](LessonDao.kt) | Транзакционное наблюдение; addOnce по UUID+агрегату, updateExisting без смены position; knownChildren — только по актуальным участникам, атомарная запись участников с именами; транзакционное удаление deleteExisting (NotFound, иначе Success с удалённым занятием, связи — по CASCADE) |

Потребитель — [репозитории](../repository/PACKAGE.md). Время не переводится в UTC. REPLACE родителя не используется.

## Проверки

- [LessonValidationTest.kt](../../../../../../test/java/com/logoped_plus/data/local/LessonValidationTest.kt) — test, правила валидации участников (`invalidReason`): исторические участники валидны, пустой список и дубли ID — InvalidData.
