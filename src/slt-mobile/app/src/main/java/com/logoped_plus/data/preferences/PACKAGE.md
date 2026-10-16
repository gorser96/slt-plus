# com.logoped_plus.data.preferences

Настройки приложения в SharedPreferences: тумблер «Скрывать выходные» и диапазон часов сетки. [Общий указатель](../../../../../../../../PACKAGES.md).

| Файл | Назначение |
|---|---|
| [SettingsStore.kt](SettingsStore.kt) | Файл `settings`, ключи `hide_weekend` (дефолт `false`), `grid_start_hour` (дефолт `0`), `grid_end_hour` (дефолт `24`); `hideWeekend: StateFlow<Boolean>`, `gridStartHour`/`gridEndHour: StateFlow<Int>` и setters |

Создаётся один экземпляр в [AppContainer](../../PACKAGE.md). Потребители — [экран настроек](../../ui/screen/PACKAGE.md) и [расписание](../../ui/screen/schedule/PACKAGE.md) (подписка на диапазон часов сетки). Не Repository и не Use Case: одиночное значение не оправдывает предметный контракт ([ARCHITECTURE.md](../../../../../../../../ARCHITECTURE.md), §25). DataStore не введён.

## Проверки

На диске тестов для пакета нет: сохранение — штатный механизм SharedPreferences, проверяется вручную перезапуском приложения.
