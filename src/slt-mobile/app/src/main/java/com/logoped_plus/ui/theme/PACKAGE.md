# com.logoped_plus.ui.theme

Назначение: тема Material 3 для Compose. [Общий указатель](../../../../../../../../PACKAGES.md).

| Файл / символ | Ответственность |
|---|---|
| [Theme.kt](Theme.kt) — `LogopedPlusTheme` | Выбор динамической, тёмной или светлой схемы и передача `Typography` в `MaterialTheme` |
| [Color.kt](Color.kt) | Цвета `Purple80`, `PurpleGrey80`, `Pink80`, `Purple40`, `PurpleGrey40`, `Pink40` |
| [Type.kt](Type.kt) — `Typography` | Настройка `bodyLarge`; остальные стили используют значения Material по умолчанию |

Тема подключена в [MainActivity.kt](../../MainActivity.kt), см. [корневой пакет](../../PACKAGE.md), и используется всем UI. При `dynamicColor = true` на Android 12+ цвета берутся из системы, поэтому изменение `Color.kt` может не менять вид приложения. Тёмный режим по умолчанию следует системе.

XML-тема Android находится отдельно: [res/values/themes.xml](../../../../../res/values/themes.xml), её подключает manifest. Для оформления Compose начинай здесь, для темы окна Android — с XML и manifest. Специализированных тестов темы пока нет.
