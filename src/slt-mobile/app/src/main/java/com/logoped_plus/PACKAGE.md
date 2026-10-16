# com.logoped_plus

Запуск Android, зависимости и навигация. [Общий указатель](../../../../../../PACKAGES.md).

| Файл | Назначение |
|---|---|
| [MainActivity.kt](MainActivity.kt) | Edge-to-edge, Compose App и LogopedPlusTheme |
| [App.kt](App.kt) | Activity-scoped ViewModel, четыре раздела и выход; busy (Saving/Generating/удаление любого экрана, перетаскивание или ошибка порядка тэгов) блокирует меню/жесты/уход; TopAppBar вместо «гамбургера» показывает стрелку назад при открытой карточке (ребёнок → closeCard, занятие → CancelCreating/CancelEditing/CloseLesson по режиму редактора); фабрики получают SettingsStore (дети — для лимита тэгов) и TagRepository (расписание — для тэгов участников); перегрузка с ViewModel для проверки реального App |
| [AppContainer.kt](AppContainer.kt) | Общая Room БД v4, три Room repository, use case генерации занятий (Clock), общие настройки, сканер видео устройства и use case выбора видео, IO scope на процесс |
| [LogopedPlusApplication.kt](LogopedPlusApplication.kt) | Application-владелец общего контейнера |

Связи: [репозитории](data/repository/PACKAGE.md), [настройки](data/preferences/PACKAGE.md), [видео устройства](data/media/PACKAGE.md), [экраны](ui/screen/PACKAGE.md), [drawer](ui/PACKAGE.md). По умолчанию расписание. Back из корневых разделов (Дети, Тэги, Настройки) возвращает в расписание; вложенные экраны (карточка/редактор, диалоги) перехватывают Back раньше. Раздел сохраняется при повороте через rememberSaveable, записи и черновики — через ViewModel. После commit ожидание снимка не блокирует навигацию.

## Проверки

На диске тестов для пакета нет; навигация проверялась осмотром кода.
