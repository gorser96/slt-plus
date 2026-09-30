# com.logoped_plus

Запуск Android, зависимости и навигация. [Общий указатель](../../../../../../PACKAGES.md).

| Файл | Назначение |
|---|---|
| [MainActivity.kt](MainActivity.kt) | Edge-to-edge, Compose App и LogopedPlusTheme |
| [App.kt](App.kt) | Activity-scoped ViewModel, разделы и выход; Saving блокирует меню/жесты/уход; перегрузка с ViewModel для проверки реального App |
| [AppContainer.kt](AppContainer.kt) | Общая Room БД v2, два Room repository, общие настройки, сканер видео устройства и use case выбора видео, IO scope на процесс |
| [LogopedPlusApplication.kt](LogopedPlusApplication.kt) | Application-владелец общего контейнера |

Связи: [репозитории](data/repository/PACKAGE.md), [настройки](data/preferences/PACKAGE.md), [видео устройства](data/media/PACKAGE.md), [экраны](ui/screen/PACKAGE.md), [drawer](ui/PACKAGE.md). По умолчанию расписание. Back из других разделов возвращает туда, из календаря — открывает подтверждение выхода, из редактора обрабатывается ScheduleScreen. Раздел сохраняется при повороте через rememberSaveable, запись и черновики — через ViewModel. После commit ожидание снимка не блокирует навигацию.

## Проверки

На диске тестов для пакета нет; навигация проверялась вручную.
