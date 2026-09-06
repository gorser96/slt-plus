# com.logoped_plus.ui

Назначение: список разделов и боковое меню. [Общий указатель](../../../../../../../PACKAGES.md).

| Файл / символ | Ответственность |
|---|---|
| [AppScreen.kt](AppScreen.kt) — `AppScreen` | Enum разделов `Schedule`, `Children`, `Settings` |
| [AppDrawer.kt](AppDrawer.kt) — `AppDrawer` | Пункты меню, выделение текущего раздела, callback `onScreenSelected` |

Текущий раздел, заголовок, закрытие drawer и показ [экранов](screen/PACKAGE.md) находятся в [App.kt](../App.kt), см. [карту корневого пакета](../PACKAGE.md). Здесь нет NavController и графа Navigation Compose.

При добавлении раздела проверь enum, пункты drawer и ветки `when` в `App`. Оформление задаёт [ui.theme](theme/PACKAGE.md). Специализированных тестов меню пока нет.
