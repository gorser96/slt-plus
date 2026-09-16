# com.logoped_plus.ui.screen.schedule

Календарь, единые черновики и подтверждение записи. [Общий указатель](../../../../../../../../../PACKAGES.md).

| Файл | Назначение |
|---|---|
| [ScheduleViewModel.kt](ScheduleViewModel.kt) | Владелец черновиков; объединение снимков по ID, Saving, sessionId, согласование commit/Flow |
| [ScheduleViewModelFactory.kt](ScheduleViewModelFactory.kt) | Фабрика с двумя repository |
| [ScheduleAction.kt](ScheduleAction.kt) | Начало/отмена, изменения полей с sessionId, ConfirmLesson, повтор чтения и календарные действия |
| [ScheduleUiState.kt](ScheduleUiState.kt) | Два состояния загрузки, editor/detailsDraft, awaitingSnapshot, календарь и UI-модели |
| [LessonEditorState.kt](LessonEditorState.kt) | CREATE/DETAILS/EDIT, UUID, sessionId, raw поля, исходный агрегат, Idle/Saving/Failure |
| [ScheduleModels.kt](ScheduleModels.kt) | MONTH/WEEK, начало недели в понедельник |
| [MonthScheduleView.kt](MonthScheduleView.kt) | Сетка месяца, выбранный день, стабильная сортировка по времени |
| [WeekScheduleView.kt](WeekScheduleView.kt) | Недельная сетка и диалог дополнительных занятий |
| [WeekTimeline.kt](WeekTimeline.kt) | Пересечения, полночь, видимые/дополнительные занятия и свободные часы |
| [ScheduleComponents.kt](ScheduleComponents.kt) | Карточки месяца и недели |
| [LessonCreateView.kt](LessonCreateView.kt) | Управляемая форма создания и общая LessonForm |
| [LessonEditView.kt](LessonEditView.kt) | Управляемая копия черновика деталей через LessonForm |
| [LessonDetailsView.kt](LessonDetailsView.kt) | Сведения, управляемые комментарий/URI и сохранение изменений |

Маршрут: [ScheduleScreen](../ScheduleScreen.kt) → ScheduleAction → ScheduleViewModel → [контракт](../../../domain/repository/PACKAGE.md) → [Room repository](../../../data/repository/PACKAGE.md). UI-модель — [model](model/PACKAGE.md), поля и видео — [component](component/PACKAGE.md).

Создание получает выбранную дату/свободный час, 40 минут, новый UUID сессии, пустые материалы. Невалидный raw ввод остаётся. Details→Edit копирует черновик; отмена возвращает исходные детали. Success создания закрывает форму, edit возвращает обновлённые детали, детали меняют сохранённую основу. Loading/Error не уничтожают ввод. Имена обновляются по ID.

Saving блокирует изменения и уход. После commit подтверждённый агрегат накладывается до равного Ready: новые записи ждут, навигация доступна. Ошибка чтения предлагает чтение, не повтор подтверждённой записи. Activity-scoped ViewModel переживает поворот; смерть процесса сохраняет только БД.

WeekTimeline сохраняет входной порядок пересечений. 90 минут занимают 1,5 часа; полночь разбивает занятие по дням. Скрытые занятия доступны через «ещё N». Двойное нажатие свободного часа передаёт дату/время в StartCreatingLesson. Сетка первоначально показывает 08:00–13:00 и прокручивается по 24 часам. Расчёт не изменён функцией 010.

## Проверки

- [FakeLessonRepository.kt](../../../../../../../test/java/com/logoped_plus/ui/screen/schedule/FakeLessonRepository.kt) — test.
- [ScheduleViewModelTest.kt](../../../../../../../test/java/com/logoped_plus/ui/screen/schedule/ScheduleViewModelTest.kt) — test.
- [WeekTimelineTest.kt](../../../../../../../test/java/com/logoped_plus/ui/screen/schedule/WeekTimelineTest.kt) — test.

Результаты и открытые критерии — [приёмка 010](../../../../../../../../../openspec/verification/persist-lessons/validation.md).
