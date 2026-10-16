# Карта пакетов «Логопед+»

Android-приложение на Kotlin и Jetpack Compose, один Gradle-модуль `app`. Ниже перечислены все 17 пакетов, объявленных в исходниках. `data`, `domain` и другие промежуточные каталоги без собственных классов не являются отдельными пунктами. Тестовые source sets используют те же имена пакетов и описаны в соответствующих картах.

## Выбор пакета по задаче

| Package / описание | Что искать |
|---|---|
| [com.logoped_plus](app/src/main/java/com/logoped_plus/PACKAGE.md) | Запуск, создание зависимостей, переключение экранов (4 раздела), системная кнопка «Назад», выход |
| [com.logoped_plus.data.local](app/src/main/java/com/logoped_plus/data/local/PACKAGE.md) | Room-схема v4 (миграции 1→2, 2→3, 3→4), DAO детей/занятий/тэгов/шаблонов, файловая БД |
| [com.logoped_plus.data.media](app/src/main/java/com/logoped_plus/data/media/PACKAGE.md) | Видео устройства через MediaStore (реализация `VideoLibrary`) |
| [com.logoped_plus.data.preferences](app/src/main/java/com/logoped_plus/data/preferences/PACKAGE.md) | Настройки приложения в SharedPreferences: «Скрывать выходные», часы сетки, число показываемых тэгов |
| [com.logoped_plus.data.repository](app/src/main/java/com/logoped_plus/data/repository/PACKAGE.md) | Room-хранение детей, занятий, тэгов и шаблонов, атомарная карточка, пакетная генерация |
| [com.logoped_plus.domain.model](app/src/main/java/com/logoped_plus/domain/model/PACKAGE.md) | Структуры ребёнка (тэги, шаблон), занятия, тэгов, видеовложения; чистая проверка шаблона |
| [com.logoped_plus.domain.repository](app/src/main/java/com/logoped_plus/domain/repository/PACKAGE.md) | Контракты доступа к детям, занятиям, тэгам и видео устройства; результат генерации |
| [com.logoped_plus.domain.usecase](app/src/main/java/com/logoped_plus/domain/usecase/PACKAGE.md) | Use case выбора видео по интервалу занятия; чистый планировщик периодических занятий |
| [com.logoped_plus.ui](app/src/main/java/com/logoped_plus/ui/PACKAGE.md) | Боковое меню и список разделов (включая «Тэги») |
| [com.logoped_plus.ui.components](app/src/main/java/com/logoped_plus/ui/components/PACKAGE.md) | Единые контролы ввода: время «ЧЧ:ММ» (`TimeField`, `parseTimeText`) и числа (`NumberField`) |
| [com.logoped_plus.ui.screen](app/src/main/java/com/logoped_plus/ui/screen/PACKAGE.md) | Экраны разделов: дети (поиск/карточка), тэги, расписание, настройки |
| [com.logoped_plus.ui.screen.children](app/src/main/java/com/logoped_plus/ui/screen/children/PACKAGE.md) | Состояние списка и карточки ребёнка, добавление, тэги, шаблон, генерация занятий, удаление |
| [com.logoped_plus.ui.screen.schedule](app/src/main/java/com/logoped_plus/ui/screen/schedule/PACKAGE.md) | Календарь, неделя, день, действия, состояние, создание/просмотр/редактирование/удаление занятия |
| [com.logoped_plus.ui.screen.schedule.component](app/src/main/java/com/logoped_plus/ui/screen/schedule/component/PACKAGE.md) | Выбор нескольких детей, поиск по имени, выбор/открытие/открепление видео |
| [com.logoped_plus.ui.screen.schedule.model](app/src/main/java/com/logoped_plus/ui/screen/schedule/model/PACKAGE.md) | Данные занятия для отображения, имена детей и URI |
| [com.logoped_plus.ui.screen.tags](app/src/main/java/com/logoped_plus/ui/screen/tags/PACKAGE.md) | Справочник «Тэги»: состояние, палитра цветов, редактор, удаление |
| [com.logoped_plus.ui.theme](app/src/main/java/com/logoped_plus/ui/theme/PACKAGE.md) | Compose-тема, динамические цвета, типографика |

## Основные маршруты

- Запуск: `AndroidManifest.xml` → `MainActivity` → `LogopedPlusTheme` → `App` → выбранный экран.
- Изменение занятия: `ScheduleScreen` → форма из `ui.screen.schedule` → `ScheduleAction` → `ScheduleViewModel` → `LessonRepository` → `RoomLessonRepository` → обновлённый `ScheduleUiState`.
- Изменение имени/тэгов/шаблона ребёнка: `ChildrenScreen` → карточка (`ChildCardView`) → `ChildrenViewModel` → `ChildRepository.saveCard`/`TagRepository` → `RoomChildRepository` → `ChildDao.saveCardOnce` (одна транзакция: имя + child_tags + child_schedule(_slots)) → observeCard обновляет карточку, Flow обновляет список и расписание.
- Периодические занятия: карточка «Создать занятия» → `ChildrenViewModel.generateLessons` → `GenerateChildLessonsUseCase` (Clock) → `LessonRepository.generateChildLessons` → `RoomLessonRepository` → `LessonDao.generateForChild` (одна Room-транзакция: чтение актуального шаблона и занятий ребёнка, чистый `RecurringLessonPlanner`, пакетная вставка) → счётчики в карточке, расписание обновляется через Flow.
- Справочник тэгов: `TagsScreen` → `TagsViewModel` → `TagRepository` → `RoomTagRepository` → `TagDao` (saveOnce/deleteOnce); назначения в карточке читают тот же справочник, удалённый тэг отвязывается по CASCADE.
- Удаление ребёнка: карточка → `DeleteChildDialog` → `ChildrenViewModel.delete` → `ChildRepository.deleteChild` → `RoomChildRepository` → `ChildDao.deleteOnce` (снимок имени участников + удаление; тэги и шаблон — по CASCADE) → Flow обновляет список детей и расписание (занятия остаются, имена — снимки в `LessonUiModel`).
- Видеовложение: `VideoAttachmentsEditor` → черновик формы/экрана → `UpdateLesson` → `VideoAttachment`. Открытие URI выполняет `VideoAttachmentLink`.
- Автоподкрепление видео: `VideoAttachmentsEditor` (кнопка, разрешение `READ_MEDIA_VIDEO`) → `AutoAttachLessonVideos` → `ScheduleViewModel` → `FindLessonVideosUseCase` → `MediaStoreVideoScanner` → черновик через `ChangeVideos`.
- Настройка «Скрывать выходные»: `SettingsScreen` → `SettingsViewModel` → `SettingsStore` → SharedPreferences; `ScheduleViewModel` подписывается на store и передаёт флаг в `WeekScheduleView` (пять/семь столбцов).

## Вне пакетов

| Путь | Назначение |
|---|---|
| [OpenSpec](../../openspec/README.md), [настройки](../../openspec/config.yaml) | Разработка по спецификациям и общие правила проекта |
| [Контракты OpenSpec](../../openspec/README.md), [статус проверок](../../openspec/verification/README.md) | Требования функций и незавершённая приёмка |
| [app/src/main/AndroidManifest.xml](app/src/main/AndroidManifest.xml) | Activity запуска, тема Android, backup, параметры приложения |
| [app/src/main/res](app/src/main/res) | Ресурсы Android: имя приложения, XML-тема, иконки, backup-правила; многие UI-строки находятся прямо в Kotlin |
| [app/build.gradle.kts](app/build.gradle.kts) | SDK, Compose и зависимости приложения |
| [settings.gradle.kts](settings.gradle.kts), [build.gradle.kts](build.gradle.kts), [gradle/libs.versions.toml](gradle/libs.versions.toml) | Модули, плагины, версии зависимостей |
| [ARCHITECTURE.md](ARCHITECTURE.md), [описание проекта.md](<описание проекта.md>) | Архитектурный план и продуктовый контекст |

Дети, занятия, тэги и шаблоны сохраняются в Room БД между запусками. Начальные списки пусты. Общий AppContainer принадлежит LogopedPlusApplication, зависимости передаются вручную. Hilt и Navigation Compose не подключены. Robolectric подключён только как testImplementation для локальных проверок Room (без устройства).
