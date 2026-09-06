# com.logoped_plus.ui.screen

Назначение: верхнеуровневые экраны разделов. [Общий указатель](../../../../../../../../PACKAGES.md).

| Файл / символ | Ответственность |
|---|---|
| [ScheduleScreen.kt](ScheduleScreen.kt) — `ScheduleScreen`, `ScheduleContent`, `ScheduleViewModeSelector` | Подписка на состояние; выбор редактирования, деталей, создания или календаря; отправка `ScheduleAction`; обработка «Назад»; черновики комментария и URI через `rememberSaveable` |
| [ChildrenScreen.kt](ChildrenScreen.kt) — `ChildrenScreen` | Список детей, FAB добавления, общий диалог добавления/переименования; локальные поля диалога через `remember` |
| [SettingsScreen.kt](SettingsScreen.kt) — `SettingsScreen` | Текстовая заглушка настроек |

Экраны подключает [App](../../PACKAGE.md). Логику детей ищи в [children](children/PACKAGE.md), действия, состояние и представления календаря — в [schedule](schedule/PACKAGE.md). Черновики видео передаются в [компоненты расписания](schedule/component/PACKAGE.md).

Для несохранённых комментариев/видео, переходов между деталями и формой, кнопок сохранения и возврата начинай с `ScheduleContent`. Данные черновиков попадают в repository через `UpdateLesson`; локальное изменение поля само по себе не сохраняет занятие. Создание получает выбранную дату с начальным временем `00:00`. Инструментальных тестов пользовательских сценариев этих экранов пока нет.
