# com.logoped_plus.ui

Боковое меню и идентификаторы разделов. [Общий указатель](../../../../../../../PACKAGES.md).

| Файл | Назначение |
|---|---|
| [AppDrawer.kt](AppDrawer.kt) | Четыре пункта (Расписание, Дети, Тэги, Настройки): выделение раздела, enabled, семантика disabled и guard callback |
| [AppScreen.kt](AppScreen.kt) | Schedule, Children, Tags, Settings |

Навигацией владеет [App](../PACKAGE.md). Во время busy (запись/генерация/удаление любого экрана) недоступны открытые пункты drawer; App отдельно проверяет актуальный статус, блокирует кнопку меню, жесты и Back. Navigation Compose не внедрён.

## Проверки

Результаты и открытые критерии — [приёмка 010](../../../../../../../openspec/verification/persist-lessons/validation.md).
