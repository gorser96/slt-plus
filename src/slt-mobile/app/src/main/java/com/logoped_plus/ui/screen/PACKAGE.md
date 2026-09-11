# com.logoped_plus.ui.screen

Назначение: верхнеуровневые экраны разделов. [Общий указатель](../../../../../../../../PACKAGES.md).

| Файл / символ | Ответственность |
|---|---|
| [ScheduleScreen.kt](ScheduleScreen.kt) — `ScheduleScreen`, `ScheduleContent`, `ScheduleViewModeSelector` | Подписка на состояние; выбор редактирования, деталей, создания или календаря; отправка `ScheduleAction`; обработка «Назад»; черновики комментария и URI через `rememberSaveable` |
| [ChildrenScreen.kt](ChildrenScreen.kt) — `ChildrenScreen` | Список детей, FAB добавления, общий диалог добавления/переименования; редактор и статус записи в ChildrenViewModel |
| [SettingsScreen.kt](SettingsScreen.kt) — `SettingsScreen` | Текстовая заглушка настроек |

В режиме месяца прокручивается весь календарный экран. В режиме недели `WeekScheduleView` занимает оставшуюся высоту между переключателем и кнопкой добавления; внутри него прокручивается часовая сетка.

Экраны подключает [App](../../PACKAGE.md). Логику детей ищи в [children](children/PACKAGE.md), действия, состояние и представления календаря — в [schedule](schedule/PACKAGE.md). Черновики видео передаются в [компоненты расписания](schedule/component/PACKAGE.md).

Для несохранённых комментариев/видео, переходов между деталями и формой, кнопок сохранения и возврата начинай с `ScheduleContent`. Данные черновиков попадают в repository через `UpdateLesson`; локальное изменение поля само по себе не сохраняет занятие. Кнопка добавления открывает создание на выбранную дату в `00:00`; двойное нажатие на свободный час недели передаёт дату и час ячейки через `StartCreatingLesson` и `creationDateTime`. Проверки перечислены ниже.

ChildrenLoadStatus различает Loading/Ready/Error и даёт повтор чтения внутри открытого диалога. ScheduleContent сохраняет формы при ошибке и передаёт childrenReady всем путям сохранения.

Тесты: [ChildrenScreenTest.kt](../../../../../../androidTest/java/com/logoped_plus/ui/screen/ChildrenScreenTest.kt), [ScheduleScreenTest.kt](../../../../../../androidTest/java/com/logoped_plus/ui/screen/ScheduleScreenTest.kt).
