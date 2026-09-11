# Карта пакетов «Логопед+»

Android-приложение на Kotlin и Jetpack Compose, один Gradle-модуль `app`. Ниже перечислены все 12 пакетов, объявленных в исходниках. `data`, `domain` и другие промежуточные каталоги без собственных классов не являются отдельными пунктами. Тестовые source sets используют те же имена пакетов и описаны в соответствующих картах.

## Выбор пакета по задаче

| Package / описание | Что искать |
|---|---|
| [com.logoped_plus](app/src/main/java/com/logoped_plus/PACKAGE.md) | Запуск, создание зависимостей, переключение экранов, системная кнопка «Назад», выход |
| [com.logoped_plus.data.local](app/src/main/java/com/logoped_plus/data/local/PACKAGE.md) | Room-схема, DAO, файловая БД детей |
| [com.logoped_plus.data.repository](app/src/main/java/com/logoped_plus/data/repository/PACKAGE.md) | Room-хранение детей, пустое хранение занятий в памяти, проверки repository |
| [com.logoped_plus.domain.model](app/src/main/java/com/logoped_plus/domain/model/PACKAGE.md) | Структуры ребёнка, занятия, видеовложения |
| [com.logoped_plus.domain.repository](app/src/main/java/com/logoped_plus/domain/repository/PACKAGE.md) | Контракты доступа к детям и занятиям |
| [com.logoped_plus.ui](app/src/main/java/com/logoped_plus/ui/PACKAGE.md) | Боковое меню и список разделов |
| [com.logoped_plus.ui.screen](app/src/main/java/com/logoped_plus/ui/screen/PACKAGE.md) | Экран детей, переключение представлений расписания, черновики, заглушка настроек |
| [com.logoped_plus.ui.screen.children](app/src/main/java/com/logoped_plus/ui/screen/children/PACKAGE.md) | Состояние списка детей, добавление, переименование, проверка имени |
| [com.logoped_plus.ui.screen.schedule](app/src/main/java/com/logoped_plus/ui/screen/schedule/PACKAGE.md) | Календарь, неделя, действия, состояние, создание/просмотр/редактирование занятия |
| [com.logoped_plus.ui.screen.schedule.component](app/src/main/java/com/logoped_plus/ui/screen/schedule/component/PACKAGE.md) | Выбор нескольких детей, поиск по имени, выбор/открытие/открепление видео |
| [com.logoped_plus.ui.screen.schedule.model](app/src/main/java/com/logoped_plus/ui/screen/schedule/model/PACKAGE.md) | Данные занятия для отображения, имена детей и URI |
| [com.logoped_plus.ui.theme](app/src/main/java/com/logoped_plus/ui/theme/PACKAGE.md) | Compose-тема, динамические цвета, типографика |

## Основные маршруты

- Запуск: `AndroidManifest.xml` → `MainActivity` → `LogopedPlusTheme` → `App` → выбранный экран.
- Изменение занятия: `ScheduleScreen` → форма из `ui.screen.schedule` → `ScheduleAction` → `ScheduleViewModel` → `LessonRepository` → `InMemoryLessonRepository` → обновлённый `ScheduleUiState`.
- Изменение имени ребёнка: `ChildrenScreen` → `ChildrenViewModel` → `ChildRepository.state` → обновление списка детей и подписанного `ScheduleViewModel` → новые имена в `LessonUiModel`.
- Видеовложение: `VideoAttachmentsEditor` → черновик формы/экрана → `UpdateLesson` → `VideoAttachment`. Открытие URI выполняет `VideoAttachmentLink`.

## Вне пакетов

| Путь | Назначение |
|---|---|
| [AGENTS.md](AGENTS.md) | Алгоритм поиска и поддержание карт |
| [SDD.md](SDD.md), [конституция](.specify/memory/constitution.md) | Разработка по спецификациям и общие правила проекта |
| [specs/README.md](specs/README.md) | Примеры спецификаций реализованных функций и основания в исходниках |
| [app/src/main/AndroidManifest.xml](app/src/main/AndroidManifest.xml) | Activity запуска, тема Android, backup, параметры приложения |
| [app/src/main/res](app/src/main/res) | Ресурсы Android: имя приложения, XML-тема, иконки, backup-правила; многие UI-строки находятся прямо в Kotlin |
| [app/build.gradle.kts](app/build.gradle.kts) | SDK, Compose и зависимости приложения |
| [settings.gradle.kts](settings.gradle.kts), [build.gradle.kts](build.gradle.kts), [gradle/libs.versions.toml](gradle/libs.versions.toml) | Модули, плагины, версии зависимостей |
| [ARCHITECTURE.md](ARCHITECTURE.md), [описание проекта.md](<описание проекта.md>) | Архитектурный план и продуктовый контекст |

Дети сохраняются в Room БД между запусками; занятия остаются в памяти процесса. Начальные списки пусты. Общий AppContainer принадлежит LogopedPlusApplication, зависимости передаются вручную. Hilt и Navigation Compose не подключены.