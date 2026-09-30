# Tasks

## 1. Data и контракт

- [x] 1.1 `LessonDao.kt`: `deleteLesson(id)` + `@Transaction deleteExisting(id)` (NotFound, иначе Success с удалённым занятием); `LessonRepository.kt`: `deleteLesson(id)`; `RoomLessonRepository.kt`: реализация по правилам `write()` (NotReady до готовности справочников, CancellationException пробрасывается, исключение → StorageUnavailable). Проверить: `.\gradlew.bat :app:compileDebugKotlin` собирается.
- [x] 1.2 androidTest: расширить `RoomLessonRepositoryTest` — агрегат (участники, видео) удаляется целиком, соседние записи и их связи не тронуты, удаление несуществующего id → NotFound. Проверить: `.\gradlew.bat :app:connectedDebugAndroidTest --tests "com.logoped_plus.data.repository.RoomLessonRepositoryTest"` на эмуляторе (TESTING.md); без эмулятора явно зафиксировать «не запускалось».

## 2. UI

- [x] 2.1 `ScheduleAction.kt`: `DeleteLesson(sessionId)`; `ScheduleViewModel.kt`: хендлер (guard saving, `canDelete` = нет saving/awaitingSnapshot, справочники Ready, запись в снапшоте и режим ≠ CREATE; Saving перед запуском, `confirmed = null`, Success → `setEditor(null)`, Failure → `Failure(reason)` редактора) и автозакрытие editor DETAILS/EDIT в `refresh()` при пропавшей записи. Проверить: компиляция.
- [x] 2.2 `LessonDetailsView.kt`: иконка `Icons.Default.Delete` рядом с Edit (`enabled` по canDelete и saving) + диалог подтверждения «Удалить занятие?» (отмена/удалить, файлы видео не удаляются); `ScheduleScreen.kt` — передаёт `onDelete`/`canDelete`. Проверить: на эмуляторе детали — иконка показана, диалог открывается и закрывается, отмена оставляет запись, во время saving команда заблокирована (сценарии delete-lesson 1–4).

## 3. Ручная проверка и регрессия

- [x] 3.1 Адресная ручная проверка на эмуляторе (TESTING.md): удаление занятия с комментарием и видео — запись исчезает из месяца, недели и «ещё N», открыт календарь прежнего периода, черновики отброшены; видеофайл не удалён, общее видео сохранено у второго занятия; после перезапуска занятие не восстанавливается; повтор нажатия и поворот во время удаления — одна операция (сценарии delete-lesson 1–2, persist-lessons «Удаление агрегата»).
- [x] 3.2 `.\gradlew.bat :app:testDebugUnitTest` (регрессия, `WeekTimelineTest`). Проверить: без падений; при сбое — фикс в рамках изменения.

## 4. Карта и контракт

- [x] 4.1 Карты пакетов: `domain.repository` (`deleteLesson` в контракте), `data.local` (транзакционное удаление, CASCADE), `data.repository` (реализация), `ui.screen.schedule` (действие, canDelete/Saving, автозакрытие в `refresh()`, диалог), `PACKAGES.md`. Проверить: `git diff --check`, ссылки в картах открываются.
- [x] 4.2 При синхронизации дельт: создать `openspec/specs/delete-lesson/spec.md` по дельте в полном формате проекта (граничные условия, критерии приёмки, границы и зависимости, происхождение); `persist-lessons` — добавить требование «Удаление агрегата» и исключить «удаление занятий» из «Не входит»; `edit-lesson` — исключить «удаление занятий» из «Не входит» со ссылкой на `delete-lesson`. Проверить: `openspec validate`, отсутствие противоречий между дельтой и основными спеками.
