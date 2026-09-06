# com.logoped_plus.domain.model

Назначение: доменные структуры данных без зависимостей от Android UI. [Общий указатель](../../../../../../../../PACKAGES.md).

| Файл / символ | Данные |
|---|---|
| [Child.kt](Child.kt) — `Child` | Строковые `id` и `name` |
| [Lesson.kt](Lesson.kt) — `Lesson` | UUID по умолчанию, `childIds`, `scheduledAt: LocalDateTime`, длительность в минутах, комментарий, список видео |
| [VideoAttachment.kt](VideoAttachment.kt) — `VideoAttachment` | URI видео в виде строки; сам файл здесь не хранится |

Используется [контрактами repository](../repository/PACKAGE.md), [хранением](../../data/repository/PACKAGE.md) и ViewModel. Связь занятия с детьми задаётся ID; имена вычисляются в `ScheduleViewModel.toUiModel`, см. [расписание](../../ui/screen/schedule/PACKAGE.md) и [LessonUiModel](../../ui/screen/schedule/model/PACKAGE.md).

Здесь нет аннотаций Room, сериализации и проверок корректности полей. При изменении структуры проверь контракты, реализации, преобразование UI-модели и формы. Отдельных тестов моделей нет; использование `Lesson.copy` проверяется тестом репозитория, указанным в его карте.
