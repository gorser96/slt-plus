# Proposal

## Why

Список детей не позволяет удалить запись: добавленный по ошибке или прекративший занятия ребёнок остаётся в списке и в выборе участников занятий навсегда. При этом занятия с ребёнком удалять нельзя — они должны сохраняться как история.

## What Changes

- Новая способность удалить ребёнка из списка: команда в форме редактирования ребёнка (доступна только для существующей записи) с диалогом подтверждения; недоступна, пока данные загружаются или выполняется запись. Повторное нажатие во время удаления невозможно.
- Удаление ребёнка не удаляет его занятия: запись, дата, время, длительность, комментарий и привязки видео сохраняются. Занятие остаётся в расписании с участником-ребёнком, для которого показывается имя, имевшееся на момент удаления; число участников и тип занятия («Групповое»/«Индивидуальное») считаются по всем участникам, включая удалённых.
- Удалённый ребёнок недоступен в выборе участников нового занятия и существующего; его участие в занятии не можно восстановить через выбор.
- Модель занятия: участники хранятся в порядке занятия; у участника может храниться имя удалённого ребёнка (снимок). Правила записи: хотя бы один участник без повторов; актуальные (не удалённые) участники должны существовать в списке детей.
- Миграция БД v2→v3: таблица `lesson_participants` получает поле имени `childName` и теряет внешний ключ на `children`; существующие дети, занятия и связи сохраняются. Снимок имени делается в той же транзакции, что и удаление ребёнка.
- Новая спецификация `delete-child`; изменения требований `persist-lessons` («Полный состав записи», «Добавочная миграция схемы»).

## Capabilities

### New Capabilities

- `delete-child`: удаление ребёнка из списка — точка входа и подтверждение, результат (список без записи, занятия сохранены историей с именем), состав удаляемых и сохраняемых данных, ошибка, защита от повторного удаления.

### Modified Capabilities

- `persist-lessons`: «Полный состав записи» — имя участника, чей ребёнок удалён, берётся из снимка на момент удаления; «Добавочная миграция схемы» — добавляется миграция v2→v3 (поле `childName`, снятие внешнего ключа на `children`, сохранение данных).

Изменение без дельты: при синхронизации из раздела «Не входит» в `persist-children` исключается пункт «удаление» (правила — в `delete-child`); требования этого спека не меняются.

## Impact

- `com.logoped_plus.domain.model`: `Lesson.kt` — `participants: List<LessonParticipant>` (новый `LessonParticipant(childId, removedName?)`) вместо `childIds`.
- `com.logoped_plus.domain.repository`: `ChildRepository.kt` — `deleteChild(child)`; результат — существующий `ChildWriteResult`.
- `com.logoped_plus.data.local`: `LessonParticipantEntity.kt` (поле `childName`, без FK на `children`), `LessonDao.kt` (проверки, `insertRelations`, `invalidReason`), `LessonWithRelations.kt` (маппинг), `ChildDao.kt` (транзакционное `deleteOnce`: снимок имени + удаление), `DatabaseMigrations.kt` (`MIGRATION_2_3`), `ChildrenDatabase.kt` (version 3).
- `com.logoped_plus.data.repository`: `RoomChildRepository.kt` — реализация `deleteChild` по шаблону `write()`.
- `com.logoped_plus.ui.screen.children`: `ChildrenViewModel.kt` (состояние и действия удаления), `ChildrenScreen.kt` (команда в форме + диалог подтверждения).
- `com.logoped_plus.ui.screen.schedule`: `LessonEditorState.kt`, `ScheduleViewModel.kt` (`toUiModel`, `ChangeChildren`, `canSave`), `ScheduleUiState.kt`, `model/LessonUiModel.kt` (число участников), `LessonCreateView.kt`, `LessonEditView.kt`, `LessonDetailsView.kt`, `ScheduleComponents.kt`.
- Сервис-пробы и androidTest-хелперы: `LessonTransactionProbeService.kt`, `RoomLessonRepositoryTest.kt`, `LessonSeedTest.kt` — под новую модель (запуска только компиляция; connected-тесты отключены настройками проекта).
- Карты пакетов: `domain.model`, `domain.repository`, `data.local`, `data.repository`, `ui.screen.children`, `ui.screen.schedule`, `PACKAGES.md`.
- Без изменений: `Child`/`ChildEntity`/таблица `children`, `LessonEntity`, `LessonVideoEntity`, схема создания и редактирования полей занятия, файлы видео.
