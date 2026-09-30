# Tasks

## 1. Настройка

- [x] 1.1 Новый пакет `com.logoped_plus.data.preferences`: `SettingsStore(context)` — SharedPreferences (файл `settings`, ключ `hide_weekend`, дефолт `false`), `hideWeekend: StateFlow<Boolean>`, `setHideWeekend(Boolean)`; в `AppContainer` — общий экземпляр `settings`. Проверить: `.\gradlew.bat :app:compileDebugKotlin` собирается.
- [x] 1.2 `SettingsViewModel` + `SettingsViewModelFactory` (состояние `hideWeekend`, функция `setHideWeekend`, подписка на store) и `SettingsScreen` с `Switch` «Скрывать выходные»; `App.kt` — подключение. Проверить: компиляция; на эмуляторе Pixel 7a (TESTING.md) тумблер переключается, экран открывается из меню.

## 2. Недельная сетка

- [x] 2.1 `ScheduleUiState.weekendHidden` (дефолт `false`), `ScheduleViewModel` (+ `ScheduleViewModelFactory`, `App.kt`) — подписка на `settings.hideWeekend` в `init`; `ScheduleScreen` передаёт флаг в `WeekScheduleView(weekendHidden)`, где `dayCount = 5/7` для таймлайнов, заголовков и колонок. Проверить: компиляция.
- [x] 2.2 Ручная адресная проверка на эмуляторе: сетка 7→5 столбцов при смене тумблера; заголовки пн–пт, подпись диапазона недели без изменений; занятия субботы/воскресенья не видны; занятие пт 23:30→сб 01:00 видно в пятницу до полуночи, в субботу пусто; месячный режим и дневной список без изменений; после перезапуска приложения значение тумблера сохраняется (сценарии 19, 20 browse-schedule и 1–4 app-settings).

## 3. Регрессия и карта

- [x] 3.1 `.\gradlew.bat :app:testDebugUnitTest` (`WeekTimelineTest` — единственный существующий автотест). Проверить: без падений; при сбое — фикс в рамках изменения.
- [x] 3.2 Карта пакетов: новый `PACKAGE.md` для `data.preferences`; правки карт `com.logoped_plus` (AppContainer/настройки), `ui.screen` (реальный экран настроек, ViewModel) и `ui.screen.schedule` (флаг и подписка); `PACKAGES.md` — индекс. В затронутых картах привести раздел «Проверки» к фактическому состоянию: на диске есть только `WeekTimelineTest`, перечисленные androidTest и `ScheduleViewModelTest`/`Example*Test` отсутствуют. Проверить: `git diff --check`, ссылки в картах открываются.

## 4. Контракт

- [x] 4.1 При синхронизации дельт: в `openspec/specs/browse-schedule/spec.md` обновить «Недельную сетку» и сценарий 5 «Начального периода», добавить граничное условие про полночь в скрытый день и критерий SC-005 (пять столбцов при включённой настройке); создать `openspec/specs/app-settings/spec.md` по дельте. Проверить: `openspec validate` и отсутствие противоречий между дельтой и основной спекой.
