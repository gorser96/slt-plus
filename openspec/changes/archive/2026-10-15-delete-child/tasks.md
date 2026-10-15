# Tasks

## 1. Data и контракт

- [x] 1.1 `LessonParticipantEntity.kt`: поле `childName: String?`, убрать FK на `ChildEntity` (оставить FK→lessons CASCADE, PK и индексы); `DatabaseMigrations.kt`: `MIGRATION_2_3` (пересоздание `lesson_participants` с DDL v3, копия строк с `childName = NULL`); `ChildrenDatabase.kt`: version 3, регистрация `MIGRATION_2_3`. Проверить: `:app:compileDebugKotlin` (KSP) и экспорт `app/schemas/com.logoped_plus.data.local.ChildrenDatabase/3.json`; DDL миграции совпадает с экпортом.
- [x] 1.2 Домен и маппинг занятий: `Lesson.kt` — `participants: List<LessonParticipant>` (`LessonParticipant(childId, removedName?)`) вместо `childIds`; `LessonWithRelations.kt` (toDomain/toEntity), `LessonDao.kt` (`invalidReason`, `knownChildren` только по актуальным, `insertRelations` с именами), `RoomLessonRepository.kt` (copy списка участников). Проверить: `:app:compileDebugKotlin`.
- [x] 1.3 Удаление ребёнка: `ChildDao.kt` — `findName`, `snapshotChildName` (`childName IS NULL`), `deleteChild`, `@Transaction deleteOnce(id)` (нет имени → false, иначе снимок + удаление == 1); `ChildRepository.kt` — `deleteChild(child)`; `RoomChildRepository.kt` — реализация по шаблону `write()` (NotFound при false). Проверить: `:app:compileDebugKotlin`.

## 2. Presentation

- [x] 2.1 Расписание под новую модель: `LessonEditorState.kt` (участники, `toLesson()` с проверкой, `from()`), `ScheduleViewModel.kt` (`toUiModel`: снимок/имя из детей, `ChangeChildren` сохраняет исторических), `ScheduleUiState.kt` (`canSave` с историческими), `model/LessonUiModel.kt` (`participantCount`), `LessonCreateView.kt`/`LessonEditView.kt` (выбор — только актуальные), `LessonDetailsView.kt`/`ScheduleComponents.kt` (тип по числу участников). Проверить: `:app:compileDebugKotlin`.
- [x] 2.2 Форма и список детей: `ChildrenViewModel.kt` — `deleteTarget`/`deleting`/`deleteError`, `openDeleteConfirm(child)`, `cancelDelete()`, `delete()` (guard Ready/deleting, Success → закрыть диалог и форму, Failure(reason) → показать); `ChildrenScreen.kt` — кнопка «Удалить» в форме существующего ребёнка (`enabled = ready && !editor.saving`), диалог подтверждения (занятия сохранятся историей с именем; блокировки во время удаления). Проверить: компиляция + осмотр кода по сценариям delete-child 1–4 (эмулятор отключён настройками проекта).
- [x] 2.3 Затронутые вспомогательные файлы: `LessonTransactionProbeService.kt` (debug), `RoomLessonRepositoryTest.kt`, `LessonSeedTest.kt` (androidTest) — под `participants`. Проверить: компиляция всех source-сетей; connected-тесты не запускать.

## 3. Проверки

- [x] 3.1 Новый JVM unit-тест правил валидации участников (`invalidReason`): занятие только с историческими участниками — валидно; пустой список и дубли `childId` — InvalidData. Проверить: `:app:testDebugUnitTest --tests "полное.ИмяТеста"`.
- [x] 3.2 Регрессия: `:app:testDebugUnitTest` (полный набор). Проверить: без сбоев; при сбое KSP из-за смешения путей WSL/Windows — повтор с `-Pksp.incremental=false`; при сбое по коду — фикс в рамках изменения.
- [x] 3.3 Зафиксировать незавершённую ручную приёмку (удаление с занятия-историей, миграция v2→v3 на данных устройства, повтор нажатия/поворот) — эмуляторы и установка отключены настройками проекта.

## 4. Контракт и карта

- [x] 4.1 `openspec validate delete-child --strict`. Проверить: без ошибок; дельта и основные спеки не противоречат друг другу.
- [x] 4.2 При синхронизации дельт: создать `openspec/specs/delete-child/spec.md` в полном формате проекта (граничные условия, критерии приёмки, границы и зависимости, происхождение); `persist-lessons` — применить MODIFIED-требования; `persist-children` — исключить «удаление» из «Не входит» со ссылкой на `delete-child`; обновить `verification/README.md` (выполненные проверки + незавершённая ручная приёмка). Проверить: `openspec validate --strict`, ссылки открываются.
- [x] 4.3 Карты пакетов: `domain.model` (`LessonParticipant`), `domain.repository` (`deleteChild`), `data.local` (v3, миграция, `deleteOnce`, снимок имени), `data.repository` (реализация), `ui.screen.children` (состояние/действия удаления, кнопки), `ui.screen.schedule` (участники, исторические, `canSave`, `participantCount`), `PACKAGES.md`. Проверить: `git diff --check`, ссылки в картах открываются.
