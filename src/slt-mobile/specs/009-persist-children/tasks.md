# Задачи: Сохранение детей в БД

**Основания**: [spec.md](spec.md), [plan.md](plan.md), [research.md](research.md), [data-model.md](data-model.md), [контракты](contracts/children.md), [quickstart.md](quickstart.md).

**Статус**: основное поведение реализовано; приёмка и расширение покрытия продолжаются. Результаты — в validation.md. Пути относительно корня slt-mobile. Новые файлы создаются соответствующей задачей.

**Проверки**: автоматизированные проверки включены по принципу IV [конституции](../../.specify/memory/constitution.md) и AGENTS.md. Сначала подготовить компилируемый контракт и каркас тестируемых классов, затем подтвердить содержательное падение тестов до реализации проверяемого поведения и добиться прохождения. Отсутствие класса/среды не считать доказательством регрессии. Особый порядок общей миграции T007–T010 описан ниже; номера этих задач сохраняются для ссылок.

Формат: `- [ ] TNNN [P] [USN] действие и путь`. `[P]` означает независимые файлы после общих предпосылок; разрешённые группы перечислены ниже. Карты PACKAGE.md поддерживать при каждом добавлении/удалении файлов, итоговый аудит — T032. Тестовые фикстуры допустимы только в test/androidTest.

## Фаза 1 — Подготовка

**Цель**: подключить минимальные зависимости без изменения остального стека.

- [X] T001 Подключить Room runtime/compiler/testing 2.8.4 и KSP2 в `gradle/libs.versions.toml`, `build.gradle.kts`, `app/build.gradle.kts`; проверить разрешённый coroutines-core через dependencyInsight, согласовать coroutines-test; настроить room.schemaLocation через CommandLineArgumentProvider на `app/schemas/`. Проверить baseline KSP из research.md с фактическим Kotlin AGP и записать выбранные версии/результат в `specs/009-persist-children/research.md`; не менять AGP или другие библиотеки без установленной причины.

## Фаза 2 — Общая основа

**Цель**: схема, контракты и владелец данных. Все истории зависят от этой фазы; интеграция приложения выполняется в US1.

- [X] T002 [P] Создать `app/src/main/java/com/logoped_plus/data/local/ChildEntity.kt`, `ChildDao.kt`, `ChildrenDatabase.kt`: `position` — «INTEGER PRIMARY KEY AUTOINCREMENT», `id` — «TEXT NOT NULL, UNIQUE», `name` — «TEXT NOT NULL» и «Имя после Kotlin trim, не уникально»; чтение «ORDER BY position ASC», domain Child без position. БД `children.db`, версия 1, без seed, main-thread queries и destructive fallback (FR-001–003, FR-008–009).
- [X] T003 [P] Определить `ChildLoadState.kt` и `ChildWriteResult.kt` в `app/src/main/java/com/logoped_plus/domain/repository/`: Loading(previous), Ready(children), Error(previous, StorageUnavailable); Success/Failure с InvalidName, NotFound, Conflict, StorageUnavailable, NotReady по `specs/009-persist-children/contracts/children.md`. Подготовить замену контракта ChildRepository, применяемую вместе с потребителями в T010; не оставлять Android/Room-типы в domain (FR-004–006).
- [X] T004 После T002 выполнить `:app:assembleDebug`, проверить генерацию DAO и экспорт `app/schemas/com.logoped_plus.data.local.ChildrenDatabase/1.json`; зафиксировать фактическую совместимость или устранённую причину сбоя в `specs/009-persist-children/research.md`. Не считать одно разрешение зависимостей проверкой генерации.
- [X] T005 После T002–T004 реализовать в `app/src/main/java/com/logoped_plus/data/local/ChildDao.kt` атомарную вставку с проверкой UUID: новый ID — вставка, те же ID/имя — успех без дубля, другое имя — Conflict; UPDATE только name по id с NotFound при отсутствии строки, без INSERT OR REPLACE. Граница успеха — commit; порядок и другие строки неизменны (FR-002, FR-004, FR-009).
- [X] T006 Создать `app/src/main/java/com/logoped_plus/AppContainer.kt` и `LogopedPlusApplication.kt` с applicationContext, одной ChildrenDatabase и SupervisorJob-scope на процесс; предусмотреть общий repository и управляемое закрытие тестового контейнера. Не открывать файл синхронно при создании Application; окончательное связывание — T010 (FR-001, FR-006–007).

**Контрольная точка**: схема генерируется, типы состояний согласованы; прикладные операции ещё не объявляются реализованными.

## Фаза 3 — US1: Сохранить добавленного ребёнка (P1)

**Цель**: пустой старт, добавление и восстановление записей без сети.

**Независимая приёмка**: добавить 20 детей с двумя тёзками; после 5 завершений процесса и перезагрузки сохранить имена/ID/порядок. Чистый старт и 5 запусков без добавления остаются пустыми (SC-001, SC-005).

### Проверки

- [X] T007 [P] [US1] Создать `app/src/androidTest/java/com/logoped_plus/data/repository/RoomChildRepositoryTest.kt`: файловая БД с уникальным тестовым именем, пустой старт, 20 вставок/тёзки, trim, close/reopen с новым repository, неизменные ID/порядок, повтор UUID и Conflict без изменения строки. Закрывать scope/соединения, не использовать production-файл (FR-001–004, FR-008–009).
- [X] T008 [P] [US1] Создать `app/src/test/java/com/logoped_plus/ui/screen/children/ChildrenViewModelTest.kt` с управляемым fake: пустое имя отклоняется через «name.trim().isBlank()», крайние пробелы удаляются, внутренние сохраняются, отмена не пишет, тёзки получают разные ID, до commit форма не закрывается (FR-002–004).

### Реализация и интеграция

- [X] T009 [US1] Создать `app/src/main/java/com/logoped_plus/data/repository/RoomChildRepository.kt`: общий eager StateFlow, асинхронное DAO-наблюдение/Entity mapping, addChild/updateChild, проверка trim и Ready, Success только после commit; список не обновлять оптимистически, CancellationException пробрасывать. Начальная ошибка чтения становится Error, запись не разрушает прежние данные; расширенная проверка retry — US3 (FR-001–006, FR-009).
- [X] T010 [US1] Атомарно перевести `app/src/main/java/com/logoped_plus/domain/repository/ChildRepository.kt` на state/retryLoading/suspend-операции, связать RoomChildRepository в `AppContainer.kt`, `App.kt`, зарегистрировать Application в `app/src/main/AndroidManifest.xml`; адаптировать обращения и фабрики в `ui/screen/children/ChildrenViewModel.kt`, `ChildrenViewModelFactory.kt`, `ui/screen/schedule/ScheduleViewModel.kt`, `ScheduleViewModelFactory.kt`. Удалить `app/src/main/java/com/logoped_plus/data/repository/InMemoryChildRepository.kt`; оба экрана используют один repository, getChildById больше не вызывается (FR-001, FR-007–008).
- [X] T011 [US1] Реализовать редактор добавления в `app/src/main/java/com/logoped_plus/ui/screen/children/ChildrenViewModel.kt`: UUID на открытие формы, Closed/Editing/Saving, Saving до launch, сохранение в viewModelScope, запрет повторного подтверждения, закрытие состоянием только после Success. Временная ошибка оставляет имя; переименование завершается в US2 (FR-003–005).
- [X] T012 [US1] Адаптировать `app/src/main/java/com/logoped_plus/ui/screen/ChildrenScreen.kt` к состоянию ViewModel: Loading отдельно от Ready(emptyList), пустая подсказка/добавление, валидация, список в заданном порядке, ожидание commit, запрет операций до Ready; не закрывать форму в обработчике кнопки. Ошибки не изображать пустым списком (FR-001–006).
- [X] T013 [US1] Удалить все начальные занятия/комментарии из `app/src/main/java/com/logoped_plus/data/repository/InMemoryLessonRepository.kt`; в `app/src/test/java/com/logoped_plus/data/repository/InMemoryLessonRepositoryTest.kt` сначала добавить проверку пустого конструктора и перевести прежнюю регрессию на два явно созданных занятия, затем добиться прохождения. Убедиться, что новые пользовательские занятия по-прежнему создаются в текущем сеансе (FR-008).
- [ ] T014 [US1] Подключить Ready-снимок и список выбора в `app/src/main/java/com/logoped_plus/ui/screen/schedule/ScheduleViewModel.kt` и `ScheduleUiState.kt`: преобразовывать имена по одной карте ID без диска; сохранять начальное расписание пустым. Выполнить проверки T007–T008, T013 и сценарий добавление → выбор участника из `specs/009-persist-children/quickstart.md`, записать результат в `specs/009-persist-children/validation.md` (FR-001, FR-007–008).

**Контрольная точка**: путь добавления проверяем самостоятельно. Это первый демонстрируемый срез, не завершение всей функции; переименование и полная приёмка ошибок обязательны дальше.

## Фаза 4 — US2: Сохранить исправленное имя (P1)

**Цель**: переименование без изменения идентичности, порядка и участников занятия.

**Независимая приёмка**: создать ребёнка/занятие как предусловие, переименовать; проверить имя в обоих разделах. После нового процесса проверить список и выбор участников, не требовать восстановления занятия (SC-002).

### Проверки

- [ ] T015 [P] [US2] Дополнить `app/src/androidTest/java/com/logoped_plus/data/repository/RoomChildRepositoryTest.kt`: rename/повторное открытие файла, неизменные ID/position/число записей, изменение одного из тёзок, прежнее имя, NotFound без вставки (FR-002–003, FR-007).
- [ ] T016 [P] [US2] Дополнить `app/src/test/java/com/logoped_plus/ui/screen/children/ChildrenViewModelTest.kt` тестами переименования: текущее имя при открытии, trim/пустой ввод/отмена, update выбранного ID, закрытие только по Success (FR-002–004).
- [X] T017 [P] [US2] Создать `app/src/test/java/com/logoped_plus/ui/screen/schedule/ScheduleViewModelTest.kt`: обновление имён в списке и selectedLesson при новом снимке детей, сохранение childIds/числа занятий; данные создавать самим тестом (FR-007).

### Реализация

- [X] T018 [US2] Завершить переименование в `app/src/main/java/com/logoped_plus/ui/screen/children/ChildrenViewModel.kt` и `app/src/main/java/com/logoped_plus/ui/screen/ChildrenScreen.kt`: открытие текущего имени, updateChild того же ID, отмена без записи, тёзки и прежнее имя допустимы; использовать общую форму и статус commit (FR-002–005).
- [ ] T019 [US2] Завершить обновление всех имён и выбранной карточки в `app/src/main/java/com/logoped_plus/ui/screen/schedule/ScheduleViewModel.kt`; проверить интеграцию через `app/src/main/java/com/logoped_plus/ui/screen/ScheduleScreen.kt` без сброса черновиков и участников. Выполнить T015–T017 и SC-002 из quickstart, дописать результат в `specs/009-persist-children/validation.md` (FR-007).

## Фаза 5 — US3: Понимать результат сохранения и загрузки (P2)

**Цель**: восстановление после ошибки без потери ввода и ложного успеха.

**Независимая приёмка**: управляемая ошибка записи/чтения → сообщение и сохранённый ввод → успешный повтор ровно один раз; во время Saving закрытие заблокировано, при Loading/Error зависимые операции недоступны (SC-003–004).

### Проверки

- [X] T020 [P] [US3] Создать `app/src/androidTest/java/com/logoped_plus/data/repository/RoomChildRepositoryFailureTest.kt` с управляемым DAO: ошибка открытия/чтения → Error → retry → Ready, без конкурирующих подписок; отказ записи/откат, отсутствие ложного Success; commit успешен при последующей ошибке наблюдения; отмена coroutine не становится Failure. Для проверки смерти процесса создать `app/src/debug/java/com/logoped_plus/data/repository/TransactionProbeService.kt`, зарегистрировать отдельный процесс в `app/src/debug/AndroidManifest.xml` и добавить внешний драйвер `specs/009-persist-children/scripts/check-transaction-interruption.ps1`: реальная файловая Room БД, сигналы BEFORE_COMMIT/AFTER_COMMIT, завершение только процесса стенда и повторное чтение по протоколу quickstart. Production-переключатели и задержки не добавлять (FR-004–006, FR-009).
- [ ] T021 [P] [US3] Дополнить `app/src/test/java/com/logoped_plus/ui/screen/children/ChildrenViewModelTest.kt`: задержка commit и двойной клик, ошибка add/update сохраняет ввод, повтор использует тот же ID, отмена после ошибки не пишет, Error чтения блокирует повтор записи до Ready (FR-004–006).
- [ ] T022 [P] [US3] Создать `app/src/androidTest/java/com/logoped_plus/ui/screen/ChildrenScreenTest.kt`: Loading/empty/Error различимы; повтор/отмена, Back/outside-dismiss и ввод заблокированы в Saving; recreation Activity не создаёт вторую запись и не теряет форму. Проверить ошибку чтения при открытом диалоге: «Повторить загрузку» доступно внутри формы, запускает только чтение, сохраняет имя/ID; после Ready пользователь отдельно подтверждает запись ровно один раз. Тестовая подмена через параметры/конструкторы, без production-переключателя (FR-004–006).
- [ ] T023 [P] [US3] Дополнить `app/src/test/java/com/logoped_plus/ui/screen/schedule/ScheduleViewModelTest.kt` проверками Loading/Error против прямых CreateLesson/UpdateLesson, включая неизвестные childIds и восстановление Ready. Создать `app/src/androidTest/java/com/logoped_plus/ui/screen/ScheduleScreenTest.kt` для блокировок выбора/всех сохранений и сохранности комментария/URI/параметров после retry (FR-006–007).

### Реализация

- [X] T024 [US3] Завершить retry и обработку сбоев в `app/src/main/java/com/logoped_plus/data/repository/RoomChildRepository.kt`: одна возобновляемая подписка, previous только для просмотра, ошибки открытия внутри асинхронного пути, раздельный результат commit/чтения, никакого seed/fallback или повторной записи из retryLoading (FR-004–006, FR-008–009).
- [X] T025 [US3] Завершить обработку ошибок редактора в `app/src/main/java/com/logoped_plus/ui/screen/children/ChildrenViewModel.kt` и `app/src/main/java/com/logoped_plus/ui/screen/ChildrenScreen.kt`: сообщения по контракту, retry/отмена с прежним вводом, отдельная «Повторить загрузку» внутри открытого диалога без отправки записи и без смены имени/ID; повтор заблокирован в Loading/Saving, запись — до Ready. Обработать NotFound/Conflict без новой записи, блокировку всех способов закрытия/ввода в Saving, сохранение состояния при навигации/повороте (FR-004–006).
- [X] T026 [US3] Добавить RetryChildren, статус загрузки и защиту CreateLesson/UpdateLesson в `app/src/main/java/com/logoped_plus/ui/screen/schedule/ScheduleAction.kt`, `ScheduleUiState.kt`, `ScheduleViewModel.kt`; разрешать запись только при Ready и наличии всех ID, включая сохранение комментариев/видео из деталей (FR-006–007).
- [X] T027 [US3] Передать enabled/статус/повтор через `app/src/main/java/com/logoped_plus/ui/screen/ScheduleScreen.kt` в `ui/screen/schedule/LessonCreateView.kt`, `LessonEditView.kt`, `LessonDetailsView.kt` и `ui/screen/schedule/component/ChildMultiSelectField.kt` относительно того же `com/logoped_plus`: заблокировать overlay/открытый sheet и все кнопки записи при Loading/Error, сохранить формы в композиции и rememberSaveable-ключи, оставить просмотр календаря (FR-006–007).
- [ ] T028 [US3] Выполнить тесты T020–T023 и сценарии ошибок/повтора из `specs/009-persist-children/quickstart.md`; проверить одну запись после серии кликов, сохранённый ввод и черновики, отсутствие подмены детей примерами. Зафиксировать выполненные и недоступные проверки в `specs/009-persist-children/validation.md` (SC-003–004).

## Фаза 6 — Общая проверка и документация

- [X] T029 Выполнить `:app:testDebugUnitTest` и при наличии устройства `:app:connectedDebugAndroidTest` по `specs/009-persist-children/quickstart.md`, отдельно проверить файловый close/reopen и регрессию занятий; записать команды, результаты и ограничения в `specs/009-persist-children/validation.md`. Example*Test не считать покрытием сценариев.
- [ ] T030 Выполнить на изолированном тестовом устройстве SC-001–005 и прерывание операции до/после commit из `specs/009-persist-children/quickstart.md`: 20 детей, 5 завершений процесса, перезагрузка, пустые запуски, rename и offline. Записать доказательства/ограничения в `specs/009-persist-children/validation.md`; не заменять перезапуск процесса пересозданием Activity и не очищать рабочую установку пользователя.
- [X] T031 Актуализировать текущие сведения в `ARCHITECTURE.md`, `SDD.md`, `specs/README.md`, ссылки на новый контракт в `specs/001-add-child/spec.md` и `specs/002-rename-child/spec.md`; сохранить их ретроспективное основание, явно отделить постоянных детей от занятий текущего процесса. В `specs/009-persist-children/spec.md` менять только статус по фактической приёмке, не ослаблять требования.
- [ ] T032 Проверить полноту карт в `PACKAGES.md`, `app/src/main/java/com/logoped_plus/PACKAGE.md`, `data/local/PACKAGE.md`, `data/repository/PACKAGE.md`, `domain/repository/PACKAGE.md`, `ui/screen/PACKAGE.md`, `ui/screen/children/PACKAGE.md`, `ui/screen/schedule/PACKAGE.md`, `ui/screen/schedule/component/PACKAGE.md` (после первого пути — относительно `com/logoped_plus/`); включить новые файлы и ссылки на test/androidTest, убрать удалённый repository, не описывать отсутствующие пакеты.
- [ ] T033 Проверить относительные ссылки, отсутствие прежнего seed в `app/src/main/java/com/logoped_plus/data/repository/`, экспорт `app/schemas/com.logoped_plus.data.local.ChildrenDatabase/1.json` и `git diff --check`; сверить FR/SC с `specs/009-persist-children/validation.md` и отметить в `specs/009-persist-children/tasks.md` только реально завершённые задачи. Отсутствующие Android-проверки оставить явно невыполненными.

## Зависимости и порядок

```text
T001 → (T002 || T003) → T004 → T005 → T006
  → US1 [подготовка T009/T010 → T007 || T008 → завершение T009/T010 → T011–T014]
  → US2 T015–T019 → US3 T020–T028
  → T029 → T030 → T031 → T032 → T033
```

T004 зависит от T002, T005 также от T003; схема общей фазы консервативно объединяет эти условия.

T007–T010 — единый блок миграции, а не четыре независимо завершаемых шага. Сначала в рамках T010 применить новый ChildRepository и адаптировать сигнатуры потребителей; в рамках T009 подготовить компилируемый каркас RoomChildRepository, а в ChildrenViewModel — поверхность редактора для тестов. Каркас не должен сообщать успешную запись без commit. Затем написать и запустить T007/T008: нужны падения утверждений о поведении при компилируемых тестах. После этого реализовать поведение T009, закончить связывание T010 и продолжить T011–T014. T007/T008 получают окончательную отметку выполнения после прохождения проверок в T014; подготовка каркаса не означает завершение T009/T010. Такой порядок не требует реализации нового repository против старого интерфейса.

US2 использует инфраструктуру и редактор US1, но проверяется на явно подготовленных ребёнке/занятии. US3 проверяется на управляемых ошибках уже работающих add/update. Истории не запускать целиком параллельно: они изменяют общие ViewModel и экраны. Обработка отказа без ложного успеха закладывается в US1; US3 завершает восстановление и полную проверку, а не разрешает до этого скрывать ошибки.

## Возможности параллельной работы

- Общая основа: T002 и T003 после T001 — разные пакеты.
- US1: T007 и T008 после общей фазы и подготовки каркаса T009/T010 — файловые тесты и тесты ViewModel в разных source sets; миграция контракта в это время не выполняется параллельно.
- US2: T015, T016 и T017 после US1 — три отдельных тестовых файла.
- US3: T020, T021, T022 и T023 после US2 — независимые тестовые файлы. Не выполнять параллельно с изменением этих же файлов из другой истории.

Это примеры допустимого распределения задач, а не указание запускать агентов на этапе генерации. Обновление общей карты пакета выполнять после объединения соответствующих изменений.

## Покрытие требований

| Требование | Основные задачи реализации | Проверки |
|---|---|---|
| FR-001 | T002, T006, T009–T012 | T007, T014, T029–T030 |
| FR-002 | T002, T005, T009, T011, T018 | T007–T008, T015–T016 |
| FR-003 | T009, T011–T012, T018 | T008, T015–T016, T030 |
| FR-004 | T005, T009, T011–T012, T024–T025 | T007–T008, T020–T022 |
| FR-005 | T009, T011, T018, T024–T025 | T020–T022, T028 |
| FR-006 | T009, T012, T024–T027 | T020–T023, T028 |
| FR-007 | T010, T014, T019, T026–T027 | T017, T019, T023, T030 |
| FR-008 | T002, T010, T013 | T007, T013–T014, T030, T033 |
| FR-009 | T002, T005, T009, T024 | T007, T020, T030 |

## Стратегия поставки

Первый демонстрируемый MVP-срез — фазы 1–3 (US1): пустые списки и постоянное добавление. Затем US2 сохраняет исправления имён, US3 завершает обработку сбоев. Полная функция принимается только после всех трёх историй и общей проверки; промежуточный срез не является разрешением выпустить регрессию переименования.

Итого: 33 задачи; подготовка/основа — 6, US1 — 8, US2 — 5, US3 — 9, завершение — 5. Завершённые задачи отмечены выше. Открытые задачи включают неполное покрытие и ручную приёмку; перезагрузка отложена пользователем.
