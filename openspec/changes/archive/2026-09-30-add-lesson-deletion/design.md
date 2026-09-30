# Design

## Context

Занятия уже живут в Room (`children.db`): `lessons` + `lesson_participants` + `lesson_videos`; обе дочерние таблицы уже объявляют `ForeignKey(..., onDelete = ForeignKey.CASCADE)` на `lessons` (LessonParticipantEntity.kt, LessonVideoEntity.kt). `LessonDao` имеет транзакционные `addOnce`/`updateExisting`, удаления нет. Контракт `LessonRepository` — `state`, `retryLoading`, `addLesson`/`updateLesson` → `LessonWriteResult` (Success(lesson) / Failure: NotReady, InvalidData, UnknownChild, NotFound, Conflict, StorageUnavailable); `RoomLessonRepository.write()` блокирует запись при неготовности справочников, ловит исключения → StorageUnavailable, пробрасывает CancellationException.

UI: детали рендерит `ScheduleScreen` (режим DETAILS), `LessonDetailsView` — шапка «← Назад», заголовок, иконка Edit. `ScheduleViewModel` владеет `LessonEditorState` (mode DETAILS/EDIT/CREATE, status Idle/Saving/Failure, sessionId); `ScheduleUiState.canSave` = нет Saving/awaitingSnapshot, оба справочника Ready, запись в снапшоте (или CREATE). Действия идут через `ScheduleAction`. Существующие проверки: `WeekTimelineTest` (JVM), androidTest-тесты repository по картам пакетов.

Приложимые решения ARCHITECTURE.md: §10/§14 (контракт в Domain, реализация в Data), §15 (Room — источник истины, Flow сам обновит UI после удаления, без ручных reload), §25 (не плодить абстракции под тривиальную операцию), §26 (минимальные проверки под риск). Фактический паттерн — ViewModel вызывает repository напрямую (use cases и Hilt в проекте отсутствуют, §24 как цель не считаем реализованным); следуем фактическому.

## Goals / Non-Goals

**Goals:**
- команда удаления в деталях с диалогом подтверждения;
- атомарное постоянное удаление агрегата (запись, участники, привязки видео) без удаления файлов;
- статусы «выполняется/ошибка» по существующим правилам редактора; автозакрытие деталей при пропавшей записи.

**Non-Goals:**
- удаление из списков/сетки, пакетное удаление, отмена удаления, удаление файлов видео, удаление детей.

## Decisions

1. **Data: одна транзакционная команда в `LessonDao`.** `@Query("DELETE FROM lessons WHERE id = :id") deleteLesson(id)` + `@Transaction open suspend fun deleteExisting(id: String): LessonWriteResult`: `find(id) ?: Failure(NotFound)` → `deleteLesson(id)` → `Success(existing.toDomain())`. Связи участников и видео удаляет объявленный CASCADE (Room включает foreign_keys).
   - Альтернатива — явный `clearParticipants`/`clearVideos` + `deleteLesson`: отклонено, CASCADE уже в схеме, явное удаление дублирует схему.
   - Альтернатива — смотреть количество строк `deleteLesson`: отклонено, не даёт NotFound до удаления и доменную запись для `Success`, семантика расходится с `updateExisting`.

2. **Контракт: `LessonRepository.deleteLesson(id: String): LessonWriteResult`.** Реализация в `RoomLessonRepository` повторяет правила `write()`: оба справочника Ready, иначе NotReady; try/catch — CancellationException пробрасывается, остальное → StorageUnavailable. Результат переиспользуется: `Success.lesson` — удалённое занятие, причины (NotFound/NotReady/StorageUnavailable) достаточны; отдельный `DeleteLessonResult` не вводится (§25). Валидация полей (`invalidReason`) при удалении не нужна.

3. **Presentation: действие `ScheduleAction.DeleteLesson(sessionId)`, диалог локальный.** Состояние диалога — `remember` в `LessonDetailsView` (чисто визуально, сбрасывается при уходе; поворот лишь закрывает диалог, операция защищена пунктом 4). Кнопка в шапке рядом с Edit: `Icons.Default.Delete`, `enabled = canDelete && !editor.saving`; `AlertDialog` «Удалить занятие?» с пояснением, что файлы видео не удаляются; «Отмена» закрывает диалог, «Удалить» шлёт действие.
   - `canDelete` в ViewModel: `!saving && !awaitingSnapshot && children Ready && lessons Ready && (режим ≠ CREATE и запись в снапшоте)`. Осознанно не переиспользуем `canSave` целиком: валидные поля формы для удаления не нужны (удаляется сохранённая запись, не черновик). Блокировка при `awaitingSnapshot` — согласована с правилом блокировки записи до согласованного снимка.
   - Альтернатива — состояние диалога в `ScheduleUiState`: отклонено, увеличивает состояние ViewModel ради визуального флага.

4. **ViewModel: удаление по шаблону `confirm`.** Хендлер `DeleteLesson`: guard `saving`; редактор совпадает по `sessionId`; `setEditor(editor.copy(status = Saving))` (существующие блокировки Back/полей/жестов по Saving); перед запуском `confirmed = null` (если удаляемая запись была overlay-снимком после создания). Запуск в `viewModelScope`: Success → `setEditor(null)` (детали закрыты, календарь открыт, списки обновит Room-Flow); Failure → `setEditor(editor.copy(status = Failure(reason)))`, ошибка показывается существующим `LessonLoadStatus` (сообщения по reason, «Обновить данные» при NotFound/NotReady/Conflict). Поворот не повторяет операцию: состояние Saving в activity-scoped ViewModel, вызов в `viewModelScope`.

5. **`refresh()`: автозакрытие редактора с пропавшей записью.** Если editor в DETAILS/EDIT и его `lessonId` отсутствует в рядах (снапшот + overlay) — `setEditor(null)`. Прикрывает сценарий «NotFound → обновить данные → записи нет» и исключает пустой экран (теперь `selectedLesson == null` рендерит ничего).

6. **Проверки — под риск потери/повреждения данных.** Удаление затрагивает целостность Room-схемы (CASCADE, атомарность) — JVM-юнитом не проверить:
   - расширить androidTest `RoomLessonRepositoryTest` сценариями удаления: агрегат с участниками и видео удаляется целиком, соседние записи и их связи не тронуты, удаление несуществующего id → NotFound;
   - `.\gradlew.bat :app:testDebugUnitTest` (регрессия, `WeekTimelineTest`) + `compileDebugKotlin`;
   - ручная проверка на эмуляторе (TESTING.md): диалог (отмена/подтверждение), исчезновение из месяца/недели/«ещё N», черновики отброшены, файл видео не удалён, общее видео живёт во втором занятии, после перезапуска занятие не восстанавливается, блокировки во время удаления;
   - новые ViewModel/Compose-тесты не вводят: поток — тонкое делегирование repository + существующие правила Saving.

## Risks / Trade-offs

- [CASCADE зависит от применения Room внешним ключам] → FK уже объявлены в существующей схеме; андрoid-тест подтверждает очистку связей фактическим прочтением агрегата.
- [Удаление заблокировано, пока идёт ожидание снимка после создания] → пользователь ждёт мгновенный снимок; согласовано с правилом блокировки записи до согласованного снимка.
- [Диалог не переживает поворот] → визуально безвредно; сама операция защищена Saving в ViewModel.
- [Автозакрытие EDIT при пропавшей записи меняет `refresh()`] → состояние до изменения недостижимо (операций, стирающих запись, нет); риск минимален.
- [«Обновить данные» после ошибки удаления перезагружает справочники] → существующий `RetryLessons`/`RetryChildren`, без новой логики.

## Migration Plan

Схема Room и версия БД не меняются (CASCADE уже в v2), миграций нет. Откат — отмена коммита (Data + Domain-контракт + UI + карты); данные пользователей не затрагиваются.

## Open Questions

— (точка входа и подтверждение согласованы с пользователем: только детали, диалог подтверждения; файлы видео не удаляются).
