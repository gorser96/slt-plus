# com.logoped_plus.ui.screen

Верхнеуровневые экраны разделов. [Общий указатель](../../../../../../../../PACKAGES.md).

| Файл | Назначение |
|---|---|
| [ChildrenScreen.kt](ChildrenScreen.kt) | Список детей, диалог добавления/переименования и ChildrenLoadStatus |
| [ScheduleScreen.kt](ScheduleScreen.kt) | Подписка, маршрутизация editor, Loading/Error/повтор внутри форм, Back; собственных черновиков нет |
| [SettingsScreen.kt](SettingsScreen.kt) | Заглушка настроек |

Экраны подключает [App](../../PACKAGE.md). Бизнес-переходы — [children](children/PACKAGE.md) и [schedule](schedule/PACKAGE.md). В месяце прокручивается весь календарь; неделя получает оставшуюся высоту и собственную прокрутку сетки. Ошибка чтения не подменяется пустым расписанием. ScreenChildren/ScreenLessons в ScheduleScreenTest — управляемые тестовые репозитории.

## Проверки

- [ChildrenScreenTest.kt](../../../../../../androidTest/java/com/logoped_plus/ui/screen/ChildrenScreenTest.kt) — androidTest.
- [ScheduleScreenTest.kt](../../../../../../androidTest/java/com/logoped_plus/ui/screen/ScheduleScreenTest.kt) — androidTest.

Результаты и открытые критерии — [приёмка 010](../../../../../../../../openspec/verification/persist-lessons/validation.md).
