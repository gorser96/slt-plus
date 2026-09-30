# com.logoped_plus.data.preferences

Настройки приложения в SharedPreferences: реактивный источник тумблера «Скрывать выходные». [Общий указатель](../../../../../../../../PACKAGES.md).

| Файл | Назначение |
|---|---|
| [SettingsStore.kt](SettingsStore.kt) | Файл `settings`, ключ `hide_weekend` (дефолт `false`), `hideWeekend: StateFlow<Boolean>` и `setHideWeekend` |

Создаётся один экземпляр в [AppContainer](../../PACKAGE.md). Потребители — [экран настроек](../../ui/screen/PACKAGE.md) и [расписание](../../ui/screen/schedule/PACKAGE.md). Не Repository и не Use Case: одиночное значение не оправдывает предметный контракт ([ARCHITECTURE.md](../../../../../../../../ARCHITECTURE.md), §25). DataStore не введён.

## Проверки

На диске тестов для пакета нет: сохранение — штатный механизм SharedPreferences, проверяется вручную перезапуском приложения.
