# Tasks

## 1. Порядок тэгов: data + domain

- [x] 1.1 `TagDao`: `listAll()`, `updatePosition(id, position)`, транзакция `reorderOnce(ids)` (сверка множества, два прохода позиций)
- [x] 1.2 `TagRepository.reorderTags(orderedIds)` + реализация в `RoomTagRepository`
- [x] 1.3 Unit-тест Room: перестановка 3 тэгов меняет порядок `observeTags`; неверное множество id — `NotFound`, порядок не меняется

## 2. Порядок тэгов: drag & drop в «Тэгах»

- [x] 2.1 `TagsViewModel`/`TagsUiState`: `dragOrder`, `dragTagId`, `pendingReorder`, `reorderError`; `startDrag`, `dragMove(from, to)`, `endDrag`, `retryReorder`, `cancelReorder`; `tags` учитывает `dragOrder`
- [x] 2.2 `TagsScreen`: рукоятка перетаскивания в строке (`detectDragGestures`, высота строки через `onSizeChanged`), подсветка перетаскиваемой строки, блокировка действий во время жеста
- [x] 2.3 Блок ошибки перестановки с «Повторить»/«Отменить»

## 3. Лимит тэгов: настройка

- [x] 3.1 `SettingsStore.visibleTagsLimit` (ключ `visible_tags_limit`, default 3) + `SettingsViewModel`/`SettingsUiState` + поле на экране настроек (0–10, ошибка вне диапазона)

## 4. Ограниченный вывод тэгов

- [x] 4.1 Общий композабл `LimitedTagChips(tags, maxTags)` (чипы + метка «+N»)
- [x] 4.2 Список детей: `ChildrenViewModel`(+фабрика, `App.kt`) получает `SettingsStore`, `ChildrenUiState.visibleTagsLimit`; строка ребёнка — тэги по порядку справочника через `LimitedTagChips`
- [x] 4.3 `TagRepository` в `ScheduleViewModel`(+фабрика, `App.kt`), `ScheduleUiState.visibleTagsLimit`; `LessonUiModel.participants` (`LessonParticipantUi`), `toUiModel` (имя/снимок + тэги по порядку справочника), подписка `refresh` на тэги
- [x] 4.4 `LessonDetailsView`: участники строками «имя — тэги» через `LimitedTagChips` с лимитом

## 5. Выбор тэгов ребёнка в нижнем окне

- [x] 5.1 Редактор карточки: сводка назначенных тэгов (чипы по порядку справочника) + кнопка «Выбрать тэги»
- [x] 5.2 `ModalBottomSheet` выбора тэгов (чекбоксы, цвет, имя, «Готово», `imePadding`); назначение через существующий `toggleTag`

## 6. Мелкие правки UI

- [x] 6.1 `imePadding`: экран настроек, нижний лист выбора детей (`ChildMultiSelectField`)
- [x] 6.2 `SlotEditorRow`: иконка удаления строки — корзина (`Icons.Default.Delete`)

## 7. Проверки и карта

- [x] 7.1 `.\gradlew.bat :app:testDebugUnitTest` (полный набор)
- [x] 7.2 Обновить PACKAGE.md затронутых пакетов и PACKAGES.md (data.local, data.preferences, data.repository, domain.repository, ui.screen, ui.screen.children, ui.screen.schedule, ui.screen.schedule.model, ui.screen.tags, com.logoped_plus)
