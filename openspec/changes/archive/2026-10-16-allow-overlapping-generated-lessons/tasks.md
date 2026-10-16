# Tasks

## 1. Алгоритм и контракт

- [x] 1.1 Убрать проверку пересечений и параметр `allLessonSpans` из `RecurringLessonPlanner`, поле `conflicts` из `GenerationCounts`; обновить `LessonDao.generateForChild` (без чтения всех интервалов) и текст результата карточки; проверить компиляцией main.

## 2. Проверки

- [x] 2.1 Переделать тесты конфликтов в `RecurringLessonPlannerTest` в проверки создания пересечений (всё расписание, полночь, строки шаблона), сохранив дедупликацию; обновить счётчики в `LocalDatabaseIntegrationTest`, `ChildrenViewModelCardTagsTest` и текст в `ChildCardTagsUiTest`; запустить адресные `:app:testDebugUnitTest` и компиляцию `:app:compileDebugAndroidTestKotlin`.

## 3. Документация и приёмка

- [x] 3.1 Обновить PACKAGE.md пакетов `domain.usecase`, `domain.model`, `data.local` и маршрут генерации в PACKAGES.md; зафиксировать выполненные проверки и ограничения в openspec/verification.
- [x] 3.2 При синхронизации дельты обновить Purpose основного `child-recurring-schedule` (без «учитывая занятость расписания») и прогнать `openspec validate --specs`.
