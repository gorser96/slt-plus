# com.logoped_plus

Назначение: точка входа Android и сборка приложения. [Общий указатель](../../../../../../PACKAGES.md).

| Файл / символ | Ответственность |
|---|---|
| [MainActivity.kt](MainActivity.kt) — `MainActivity.onCreate` | Edge-to-edge, `setContent`, подключение `LogopedPlusTheme` и `App` |
| [App.kt](App.kt) — `App` | Получение общего AppContainer, создание фабрик и ViewModel; `Scaffold`, заголовок, drawer, выбор раздела, подтверждение выхода |

Начинай с `App` при изменении состава зависимостей или навигации. Раздел по умолчанию — расписание. Кнопка «Назад» из других разделов возвращает туда; на расписании открывает подтверждение выхода. Вложенные состояния расписания дополнительно обрабатываются в `ScheduleScreen`.

Связи: [репозитории](data/repository/PACKAGE.md), [меню](ui/PACKAGE.md), [экраны](ui/screen/PACKAGE.md), [ViewModel детей](ui/screen/children/PACKAGE.md), [расписание](ui/screen/schedule/PACKAGE.md), [тема](ui/theme/PACKAGE.md). Activity объявлена в [AndroidManifest.xml](../../../AndroidManifest.xml). `App` — composable-функция, а не подкласс Android `Application`.

Тесты того же package: [ExampleUnitTest.kt](../../../../test/java/com/logoped_plus/ExampleUnitTest.kt) проверяет `2 + 2`; [ExampleInstrumentedTest.kt](../../../../androidTest/java/com/logoped_plus/ExampleInstrumentedTest.kt) проверяет package name контекста на устройстве. Эти шаблоны не проверяют навигацию и жизненный цикл.

| Файл | Назначение |
|---|---|
| [AppContainer.kt](AppContainer.kt) | Общая Room БД и репозитории на процесс |
| [LogopedPlusApplication.kt](LogopedPlusApplication.kt) | Владелец контейнера с applicationContext |
