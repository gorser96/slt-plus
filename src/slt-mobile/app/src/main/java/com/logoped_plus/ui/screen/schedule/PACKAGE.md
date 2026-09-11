# com.logoped_plus.ui.screen.schedule

Назначение: состояние расписания, действия пользователя, календарь и формы занятия. [Общий указатель](../../../../../../../../../PACKAGES.md).

## Файлы и точки входа

| Файл / символ | Что искать |
|---|---|
| [ScheduleViewModel.kt](ScheduleViewModel.kt) — `onAction`, `Lesson.toUiModel` | Обработка действий, чтение/запись repository, переходы состояния, связывание ID детей с именами и доменных вложений с URI |
| [ScheduleViewModelFactory.kt](ScheduleViewModelFactory.kt) | Создание ViewModel с двумя repository |
| [ScheduleAction.kt](ScheduleAction.kt) — `ScheduleAction` | Sealed-контракт выбора даты/занятия, создания, редактирования, сохранения, закрытия и перелистывания |
| [ScheduleUiState.kt](ScheduleUiState.kt) — `ScheduleUiState` | Выбранное занятие/дата, флаги форм, месяц/неделя, списки детей и занятий |
| [ScheduleModels.kt](ScheduleModels.kt) — `ScheduleViewMode`, `LocalDate.startOfWeek` | Режимы `MONTH`/`WEEK`, начало недели в понедельник |
| [MonthScheduleView.kt](MonthScheduleView.kt) | `MonthScheduleView`, приватные `CalendarView`, `CalendarDay`, `buildCalendarDays`; сетка месяца, выбранный день и сортированный список занятий |
| [WeekScheduleView.kt](WeekScheduleView.kt) | `WeekScheduleView`, приватная `formatWeekRange`; семь колонок дней, прокручиваемая часовая сетка и диалог дополнительных занятий |
| [WeekTimeline.kt](WeekTimeline.kt) — `buildWeekDayTimeline`, `WeekDayTimeline`, `WeekLessonSpan` | Интервалы в минутах, обрезка по границам дня, видимые/дополнительные занятия, проверка свободного часа |
| [ScheduleComponents.kt](ScheduleComponents.kt) — `LessonItem`, `WeekLessonItem` | Карточки занятия для месяца и недели |
| [LessonCreateView.kt](LessonCreateView.kt) | Создание: дата через `LessonDateField`, компактные поля часов и минут рядом с датой через `LessonDateTimeFields`, дети, длительность; callback `onCreate` |
| [LessonDetailsView.kt](LessonDetailsView.kt) | Просмотр занятия, тип по количеству детей, редактирование комментария и вложений, кнопки сохранения/редактирования |
| [LessonEditView.kt](LessonEditView.kt) | Форма изменения даты, времени, детей, длительности, комментария и видео; `LessonDateTimeFields`, callback `onSave` |

## Связи и маршрут поиска

Вход и переключение представлений находятся в [ScheduleScreen.kt](../ScheduleScreen.kt), см. [родительский пакет](../PACKAGE.md). Если проблема в черновиках или возврате из формы, начинай там. Затем проследи callback → `ScheduleAction` → `ScheduleViewModel.onAction` → [контракт repository](../../../domain/repository/PACKAGE.md) → [реализацию](../../../data/repository/PACKAGE.md).

Используемые сущности: [доменные модели](../../../domain/model/PACKAGE.md), [LessonUiModel](model/PACKAGE.md). Выбор детей и видео вынесен в [component](component/PACKAGE.md). Фабрику подключает [App](../../../PACKAGE.md).

## Особенности текущего поведения

- ViewModel начинает с текущей даты, собирает `ChildRepository.state` и при изменениях обновляет имена во всех занятиях и выбранной карточке. Список занятий не имеет собственного Flow; перечитывается после операций.
- `UpdateLesson` игнорирует неизвестный ID, неположительную длительность и пустой список детей; сохраняет через `original.copy`. CreateLesson и UpdateLesson проверяют Ready, наличие всех ID, непустых участников и положительную длительность.
- Переключение режима привязывает месяц/неделю к выбранной дате; стрелки меняют отображаемый период отдельно от выбранной даты.
- Неделя получает ограниченную высоту от `ScheduleScreen`: заголовки закреплены, сетка показывает пять часовых интервалов, первоначально 08:00–13:00, и прокручивается по всем 24 часам. Положение и высота блока пропорциональны минутам начала и длительности: 90 минут занимают 1,5 часа. Занятия, пересекающие полночь, отображаются частями в соответствующих днях. При занятии общего часа приоритет имеет порядок входного списка; остальные доступны через «ещё N» в каждом занимаемом ими часу. Свободный час не пересекается ни с одним занятием, включая скрытые. Двойное нажатие на него вызывает `StartCreatingLesson` с датой и началом часа.
- `StartCreatingLesson` принимает необязательное время: `creationDateTime` передаётся форме создания, без аргумента используется выбранная дата и 00:00. Отмена и создание очищают это значение.
- Обе формы сохраняют выбранную дату и время как `LocalDateTime`; дата хранится в `rememberSaveable` как epoch day, часы и минуты — строками в `rememberSaveable`; пустое время и значения вне диапазонов 0–23/0–59 блокируют сохранение. Комментарий и видео при создании не задаются; их можно добавить после создания.
- `LessonItem` в месячном списке и диалоге «ещё N», как и детали, определяет тип числом `childIds`: больше одного — «Групповое занятие», иначе — «Индивидуальное занятие».

Расчёт недельной сетки проверяет [WeekTimelineTest.kt](../../../../../../../test/java/com/logoped_plus/ui/screen/schedule/WeekTimelineTest.kt): длительность 90 минут, смещение на полчаса, совпадения, свободные часы и переход через полночь. Проверки ViewModel и форм перечислены ниже и в карте родительского пакета. Сохранение модели частично проверяет тест, указанный в [карте data.repository](../../../data/repository/PACKAGE.md).


[ScheduleViewModelTest.kt](../../../../../../../test/java/com/logoped_plus/ui/screen/schedule/ScheduleViewModelTest.kt) проверяет смену имён и блокировку CreateLesson/UpdateLesson при Loading/Error и неизвестных ID. RetryChildren возобновляет чтение. Формы принимают childrenReady; детали также блокируют сохранение комментария/видео до Ready.
