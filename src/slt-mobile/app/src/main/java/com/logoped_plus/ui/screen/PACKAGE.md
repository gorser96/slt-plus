# com.logoped_plus.ui.screen

Верхнеуровневые экраны разделов. [Общий указатель](../../../../../../../../PACKAGES.md).

| Файл | Назначение |
|---|---|
| [ChildrenScreen.kt](ChildrenScreen.kt) | Список детей, диалог добавления/переименования и ChildrenLoadStatus |
| [ScheduleScreen.kt](ScheduleScreen.kt) | Подписка, маршрутизация editor, Loading/Error/повтор внутри форм, Back; собственных черновиков нет |
| [SettingsScreen.kt](SettingsScreen.kt) | Экран настроек: тумблер «Скрывать выходные» |
| [SettingsViewModel.kt](SettingsViewModel.kt) | Состояние `hideWeekend`, подписка на store, `setHideWeekend` |
| [SettingsViewModelFactory.kt](SettingsViewModelFactory.kt) | Фабрика с [SettingsStore](../../data/preferences/PACKAGE.md) |

Экраны подключает [App](../../PACKAGE.md). Бизнес-переходы — [children](children/PACKAGE.md) и [schedule](schedule/PACKAGE.md). Настройки хранятся в [data.preferences](../../data/preferences/PACKAGE.md) и переживают перезапуск. В месяце прокручивается весь календарь; неделя получает оставшуюся высоту и собственную прокрутку сетки. Ошибка чтения не подменяется пустым расписанием.

## Проверки

На диске тестов для пакета нет; экран настроек и тумблер проверялись вручную на эмуляторе.
