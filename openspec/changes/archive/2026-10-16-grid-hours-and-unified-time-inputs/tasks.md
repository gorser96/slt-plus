# Tasks

## 1. Единые контролы

- [x] 1.1 Новый пакет `com.logoped_plus.ui.components`: `TimeField.kt` — `TimeField(value, onValueChange, label, modifier, enabled)`: одно поле «ЧЧ:ММ», цифры из IME (до 4) с автоформатом (3 цифры — «dd:d» при первых двух ≤ 23, иначе «d:dd»), числовая клавиатура, выделение всего значения при фокусе, курсор в конец после принятого изменения, `isError`/поддерживающий текст при невалидном значении; рядом `parseTimeText(text): LocalTime?` (regex `\d{1,2}:\d{2}`, 0–23/0–59). Проверить: компиляция `:app:compileDebugKotlin`.
- [x] 1.2 `NumberField.kt` — `NumberField(value, onValueChange, label, modifier, enabled, maxDigits, isError, supportingText)`: только цифры (до `maxDigits`), числовая клавиатура, выделение при фокусе. Проверить: компиляция.
- [x] 1.3 `PACKAGE.md` нового пакета, строка в `PACKAGES.md` (16 → 17 пакетов). Проверить: ссылки открываются, `git diff --check`.

## 2. Формы и карточки на общих контролах

- [x] 2.1 `LessonEditorState.kt`: `hour`/`minute` → `time: String` (формат «ЧЧ:ММ»), `LessonTextField.HOUR/MINUTE` → `TIME`, `toLesson()` через `parseTimeText` (сохранение секунд/наносекунд исходного — по сравнению часов и минут). `ScheduleViewModel.kt`: `StartCreatingLesson` собирает `time`, `ChangeText` обрабатывает `TIME`. Проверить: компиляция.
- [x] 2.2 `LessonDateTimeFields.kt`: два поля «Часы»/«Мин» заменить общим `TimeField` (рядом с датой); в `LessonForm` (`LessonCreateView.kt`) длительность — через `NumberField` (maxDigits 4, существующие текст ошибки и блокировка при ≤ 0). Проверить: компиляция; набор «0 8 3 0» в поле «08:00» даёт «08:30» (осмотр кода: цифры → автоформат).
- [x] 2.3 `ChildCardView.kt` (`SlotEditorRow`): «Время» → `TimeField`, «Минуты» → `NumberField` (maxDigits 4, без полевого isError — как сейчас); `ChildrenProjection.parseSlotTime` заменить общим `parseTimeText`. Проверить: компиляция.
- [x] 2.4 `WeekTimelineTest.kt` не тронуть; запустить `.\gradlew.bat :app:testDebugUnitTest` (`-Pksp.incremental=false`) — без сбоев; при сбое — фикс в рамках изменения.

## 3. Настройка «Диапазон часов сетки»

- [x] 3.1 `SettingsStore.kt`: ключи `grid_start_hour` (дефолт 0), `grid_end_hour` (дефолт 24), `StateFlow<Int>` и setters. `SettingsViewModel`/`SettingsUiState` — значения и подписки. Проверить: компиляция.
- [x] 3.2 `SettingsScreen.kt`: в разделе «Расписание» поля «Начальный час»/«Конечный час» через `NumberField` (maxDigits 2); ошибка, если начало вне 0–23, конец вне 1–24 или конец не больше начала. Проверить: компиляция; сценарии 1–4 app-settings по коду.
- [x] 3.3 `ScheduleModels.kt`: `effectiveGridRange(start, end): IntRange` (строки включительно; валидно при `start in 0..23 && end in start+1..24`, иначе 0..23). Проверить: компиляция; дефолт 0/24 → 0..23.

## 4. Сетки по диапазону

- [x] 4.1 `WeekTimeline.kt`: `buildDayTimeline(date, lessons, startMinute = 0, endMinute = 1440)` — после обреза по дню оставить span'ы, целиком лежащие в `[startMinute, endMinute]`. Новые тесты в `WeekTimelineTest.kt`: span внутри диапазона сохраняется, пересекающий границу и полностью вне диапазона исключаются, прилегание к границам сохраняется. Проверить: `.\gradlew.bat :app:testDebugUnitTest --tests "com.logoped_plus.ui.screen.schedule.WeekTimelineTest"` (`-Pksp.incremental=false`) — без сбоев.
- [x] 4.2 `ScheduleUiState.kt` — `gridStartHour`/`gridEndHour`; `ScheduleViewModel.kt` (+ подписка на оба потока store); `ScheduleScreen.kt` — эффективный диапазон в `WeekScheduleView`/`DayScheduleView` (параметры `gridStartHour`/`gridEndHour`, конец исключающий). Проверить: компиляция.
- [x] 4.3 `WeekScheduleView.kt`/`DayScheduleView.kt`: строки `end - start`, подписи часов `start..end`, высота `hourHeight * rows`, span'ы от `startMinute`, `isHourEmpty`/двойной тап по абсолютным часам диапазона, начальное смещение 8 часов при `start < 8`, иначе 0; ключи `remember` с диапазоном. Проверить: компиляция; дефолты 0/24 дают исходную раскладку (24 строки, метка 24:00, смещение 8).
- [x] 4.4 Ручная приёмка по коду (эмуляторы отключены конфигурацией проекта): диапазон 08:00–21:00 — 13 строк и метка 21:00; занятие 07:30–08:30 скрыто, 08:00–09:00 видно; 23:30–01:00 при 20:00–24:00 видно в пятницу, скрыто в субботу; некорректная пара — 24 часа и ошибка в настройках; сценарии 40–43 browse-schedule.

## 5. Карта и контракт

- [x] 5.1 Карты: `PACKAGE.md` — `data.preferences` (новые ключи/StateFlow), `ui.screen` (поля диапазона), `ui.screen.schedule` (time, диапазон сеток, подписка), `ui.screen.schedule.component` (общие контролы), `ui.screen.children` (общие контролы в строках); `PACKAGES.md` — маршруты и строки пакетов. Проверить: `git diff --check`, ссылки открываются.
- [x] 5.2 `openspec validate grid-hours-and-unified-time-inputs --strict` — успешно. При синхронизации дельт: `app-settings` — новое требование; `browse-schedule` — «Недельная сетка»/«Дневная сетка»; `create-lesson` — «Дата время и длительность»; `child-recurring-schedule` — «Недельный шаблон»; новая спека `time-input-controls`.
- [x] 5.3 Финальная сборка `:app:assembleDebug` + `:app:compileDebugAndroidTestKotlin` (`-Pksp.incremental=false`) — BUILD SUCCESSFUL; section проверки в `openspec/verification/README.md` по шаблону (выполненные проверки и незавершённая ручная приёмка).

## 6. Выделение при повторном нажатии (доработка)

- [x] 6.1 `TimeField.kt`/`NumberField.kt`: полное выделение — только при входе фокуса из несфокусированного состояния (`onFocusChanged(true)` и обработчик нажатия, если поле ещё не сфокусировано); повторное нажатие на сфокусированный контрол — `editedInFocus = true`, курсор в точку нажатия, без повторного выделения; убрать неиспользуемый `FocusRequester`. Проверить: компиляция `:app:compileDebugKotlin`.
- [x] 6.2 Приёмка по коду (эмуляторы отключены конфигурацией проекта, ручная приёмка открыта — см. verification README): первый тап — полное выделение, набор заменяет значение; повторный тап — курсор в точке нажатия, набор вставляет в позицию курсора; выход из фокуса и повторный вход — выделение снова. Проверить: `openspec validate grid-hours-and-unified-time-inputs --strict`, `:app:compileDebugKotlin` и `:app:testDebugUnitTest` — BUILD SUCCESSFUL; затронутые файлы без trailing whitespace, `git diff --check` по трекующимся — успешно.
