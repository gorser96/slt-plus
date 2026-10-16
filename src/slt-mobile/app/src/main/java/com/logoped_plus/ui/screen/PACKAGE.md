# com.logoped_plus.ui.screen

Верхнеуровневые экраны разделов. [Общий указатель](../../../../../../../../PACKAGES.md).

| Файл | Назначение |
|---|---|
| [ChildrenScreen.kt](ChildrenScreen.kt) | Раздел «Дети»: список с поиском (один непробельный символ, без регистра), алфавитной сортировкой (Collator ru-RU, тёзки — по позиции) и метками назначенных тэгов (SmallTagChip) в одной строке с именем (компактные, текст с многоточием), «Ничего не найдено» отличается от пустого списка; диалог добавления имени, DeleteChildDialog; хост карточки (cardId ≠ null → ChildCardView); ChildrenLoadStatus; общие TagSwatch/TagChip/SmallTagChip |
| [ScheduleScreen.kt](ScheduleScreen.kt) | Подписка, маршрутизация editor, Loading/Error/повтор внутри форм, Back; собственных черновиков нет |
| [SettingsScreen.kt](SettingsScreen.kt) | Экран настроек: тумблер «Скрывать выходные» |
| [SettingsViewModel.kt](SettingsViewModel.kt) | Состояние `hideWeekend`, подписка на store, `setHideWeekend` |
| [SettingsViewModelFactory.kt](SettingsViewModelFactory.kt) | Фабрика с [SettingsStore](../../data/preferences/PACKAGE.md) |
| [TagsScreen.kt](TagsScreen.kt) | Раздел «Тэги»: список названий с цветом, диалог редактора (имя + палитра из 8 цветов с названием и отметкой выбора), подтверждение удаления с пояснением об отвязке от детей |

Экраны подключает [App](../../PACKAGE.md). Бизнес-переходы — [children](children/PACKAGE.md), [tags](tags/PACKAGE.md) и [schedule](schedule/PACKAGE.md). Настройки хранятся в [data.preferences](../../data/preferences/PACKAGE.md) и переживают перезапуск. Месяц, неделя и день прокручивают весь экран; недельная и дневная сетки дополнительно прокручивают часы внутри блока, в неделе заголовки дней закреплены. Ошибка чтения не подменяется пустым расписанием.

## Проверки

На диске тестов для пакета нет; экраны проверялись осмотром кода (см. [статус проверок](../../../../../../../../openspec/verification/README.md)).
