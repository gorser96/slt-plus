# Tasks

## 1. Domain: правило выбора видео

- [x] 1.1 Модель `DeviceVideo` (domain.model) и контракт `VideoLibrary` (domain.repository) — проверить компиляцией и обновив `PACKAGE.md` затронутых пакетов.
- [x] 1.2 Use case `FindLessonVideosUseCase` в новом пакете `domain.usecase` (интервал `[start, start+duration)`, зона устройства, без дубликатов) — проверить unit-тестом (границы, дубликаты, пустой результат, занятие через полночь): `.\gradlew.bat :app:testDebugUnitTest --tests "com.logoped_plus.domain.usecase.*"`.
- [x] 1.3 Карта: `PACKAGE.md` нового пакета `domain.usecase` и строка в `PACKAGES.md` — проверить ссылками и `git diff --check`.

## 2. Скан видео устройства

- [x] 2.1 `MediaStoreVideoScanner : VideoLibrary` в новом пакете `data.media` (выборка EXTERNAL_CONTENT_URI, время создания `DATE_TAKEN` с fallback на `DATE_ADDED`, IO-поток) — проверить компиляцией и осмотром кода; поведение на устройстве — ручная приёмка.
- [x] 2.2 `AppContainer`: создание сканера и use case — проверить компиляцией и обновив `PACKAGE.md` (com.logoped_plus, data.media) и `PACKAGES.md`.

## 3. UI: кнопка, разрешение, состояние

- [x] 3.1 `AndroidManifest.xml`: разрешение `READ_MEDIA_VIDEO` — проверить компиляцией и наличием строки в манифесте.
- [x] 3.2 Действие `ScheduleAction.AutoAttachLessonVideos(sessionId)`; поля `videoScanInProgress`/`videoScanMessage` в `ScheduleUiState`; обработка в `ScheduleViewModel` (guard сессии и saving, интервал из `editor.toLesson()`, сообщение без поиска при некорректных значениях) — проверить компиляцией и осмотром кода; UI-поведение — ручная приёмка.
- [x] 3.3 Кнопка и запрос разрешения с сессионным guard в `VideoAttachmentsEditor`; подключение `onAutoAttach` в `LessonDetailsView`/`LessonEditView`, `ScheduleViewModelFactory`, `App.kt`; обновление `PACKAGE.md` (ui.screen.schedule, component) — проверить компиляцией и осмотром кода; поведение (добавление в черновик, кнопка «Сохранить», отказ в разрешении) — ручная приёмка.

## 4. Проверки

- [x] 4.1 Все unit-тесты: `.\gradlew.bat :app:testDebugUnitTest` — без сбоев.
- [x] 4.2 Сборка: `.\gradlew.bat :app:assembleDebug` — успешно.
- [x] 4.3 `openspec/verification/README.md`: зафиксировать выполненные проверки и сценарии ручной приёмки (разрешение, поиск на реальном устройстве, сохранение из деталей/редактирования) — проверить наличие записи.
