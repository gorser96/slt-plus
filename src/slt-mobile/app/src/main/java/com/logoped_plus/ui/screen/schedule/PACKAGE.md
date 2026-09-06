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
| [WeekScheduleView.kt](WeekScheduleView.kt) | `WeekScheduleView`, приватные `WeekDayColumn`, `formatWeekRange`; семь колонок дней и сортировка занятий по времени |
| [ScheduleComponents.kt](ScheduleComponents.kt) — `LessonItem`, `WeekLessonItem` | Карточки занятия для месяца и недели |
| [LessonCreateView.kt](LessonCreateView.kt) | Создание: выбранная дата только для чтения, время, дети, длительность; callback `onCreate` |
| [LessonDetailsView.kt](LessonDetailsView.kt) | Просмотр занятия, тип по количеству детей, редактирование комментария и вложений, кнопки сохранения/редактирования |
| [LessonEditView.kt](LessonEditView.kt) | Форма изменения времени, детей, длительности, комментария и видео; callback `onSave`, дата только для чтения |

## Связи и маршрут поиска

Вход и переключение представлений находятся в [ScheduleScreen.kt](../ScheduleScreen.kt), см. [родительский пакет](../PACKAGE.md). Если проблема в черновиках или возврате из формы, начинай там. Затем проследи callback → `ScheduleAction` → `ScheduleViewModel.onAction` → [контракт repository](../../../domain/repository/PACKAGE.md) → [реализацию](../../../data/repository/PACKAGE.md).

Используемые сущности: [доменные модели](../../../domain/model/PACKAGE.md), [LessonUiModel](model/PACKAGE.md). Выбор детей и видео вынесен в [component](component/PACKAGE.md). Фабрику подключает [App](../../../PACKAGE.md).

## Особенности текущего поведения

- ViewModel начинает с текущей даты, собирает `ChildRepository.children` и при изменениях обновляет имена во всех занятиях и выбранной карточке. Список занятий не имеет собственного Flow; перечитывается после операций.
- `UpdateLesson` игнорирует неизвестный ID, неположительную длительность и пустой список детей; сохраняет через `original.copy`. Создание валидируется формой, а ветка `CreateLesson` в ViewModel добавляет модель без аналогичных проверок.
- Переключение режима привязывает месяц/неделю к выбранной дате; стрелки меняют отображаемый период отдельно от выбранной даты.
- В формах дата пока не редактируется, хотя действие обновления принимает `LocalDateTime`. Комментарий и видео при создании не задаются; их можно добавить после создания.
- `LessonItem` в месячном списке пока выводит фиксированный текст «Индивидуальное занятие»; в деталях тип определяется числом `childIds`.

Прямых тестов календаря, ViewModel и форм нет. Сохранение модели частично проверяет тест, указанный в [карте data.repository](../../../data/repository/PACKAGE.md).
