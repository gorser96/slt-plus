# com.logoped_plus.ui

Боковое меню и идентификаторы разделов. [Общий указатель](../../../../../../../PACKAGES.md).

| Файл | Назначение |
|---|---|
| [AppDrawer.kt](AppDrawer.kt) | Выделение раздела, enabled, семантика disabled и guard callback |
| [AppScreen.kt](AppScreen.kt) | Schedule, Children, Settings |

Навигацией владеет [App](../PACKAGE.md). Во время Saving недоступны открытые пункты drawer; App отдельно проверяет актуальный статус, блокирует кнопку меню и жесты. Navigation Compose не внедрён.

## Проверки



Результаты и открытые критерии — [приёмка 010](../../../../../../../openspec/verification/persist-lessons/validation.md).
