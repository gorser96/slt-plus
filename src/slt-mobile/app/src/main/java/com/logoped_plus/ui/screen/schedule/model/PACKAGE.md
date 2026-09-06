# com.logoped_plus.ui.screen.schedule.model

Назначение: модель занятия для Compose UI. [Общий указатель](../../../../../../../../../../PACKAGES.md).

| Файл / символ | Данные |
|---|---|
| [LessonUiModel.kt](LessonUiModel.kt) — `LessonUiModel` | `id`, дата/время, длительность, готовая строка `childNames`, комментарий, `childIds` и `videoUris` |

Создание UI-модели выполняет приватное расширение `Lesson.toUiModel` в [ScheduleViewModel.kt](../ScheduleViewModel.kt), см. [schedule](../PACKAGE.md). Оно получает имена через `ChildRepository`, пропускает ненайденных детей при построении строки и преобразует вложения в URI. Исходная сущность описана в [domain.model](../../../../domain/model/PACKAGE.md).

Потребители: `ScheduleUiState`, календарные карточки, детали и редактирование занятия, а также [ScheduleScreen](../../PACKAGE.md). Для изменения отображаемых полей проверь и модель, и mapper, и эти места использования. Сам класс не содержит бизнес-логики или Android API. Прямых тестов UI-модели и mapper пока нет.
