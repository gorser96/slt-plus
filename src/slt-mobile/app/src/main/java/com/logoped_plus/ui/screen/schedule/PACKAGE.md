# com.logoped_plus.ui.screen.schedule

Календарь, единые черновики и подтверждение записи. [Общий указатель](../../../../../../../../../PACKAGES.md).

| Файл | Назначение |
|---|---|
| [ScheduleViewModel.kt](ScheduleViewModel.kt) | Владелец черновиков; объединение снимков по ID, Saving, sessionId, согласование commit/Flow; удаление по DeleteLesson; автозакрытие деталей/редактирования в `refresh()` при пропавшей записи; автоподкрепление видео по AutoAttachLessonVideos; подписка на настройку «Скрывать выходные» |
| [ScheduleViewModelFactory.kt](ScheduleViewModelFactory.kt) | Фабрика с двумя repository, настройками и use case выбора видео |
| [ScheduleAction.kt](ScheduleAction.kt) | Начало/отмена, изменения полей с sessionId, ConfirmLesson, DeleteLesson, AutoAttachLessonVideos, повтор чтения и календарные действия |
| [ScheduleUiState.kt](ScheduleUiState.kt) | Два состояния загрузки, editor/detailsDraft, awaitingSnapshot, календарь, UI-модели, canSave/canDelete, videoScanInProgress/videoScanMessage |
| [LessonEditorState.kt](LessonEditorState.kt) | CREATE/DETAILS/EDIT, UUID, sessionId, raw поля, исходный агрегат, Idle/Saving/Failure |
| [ScheduleModels.kt](ScheduleModels.kt) | MONTH/WEEK, начало недели в понедельник |
| [MonthScheduleView.kt](MonthScheduleView.kt) | Сетка месяца, выбранный день, стабильная сортировка по времени |
| [WeekScheduleView.kt](WeekScheduleView.kt) | Недельная сетка (семь столбцов, при скрытых выходных — пять) и диалог дополнительных занятий |
| [WeekTimeline.kt](WeekTimeline.kt) | Сортировка, кластеризация пересечений, столбики, группы скрытых занятий, полночь и свободные часы |
| [ScheduleComponents.kt](ScheduleComponents.kt) | Карточки месяца и недели |
| [LessonCreateView.kt](LessonCreateView.kt) | Управляемая форма создания и общая LessonForm |
| [LessonEditView.kt](LessonEditView.kt) | Управляемая копия черновика деталей через LessonForm |
| [LessonDetailsView.kt](LessonDetailsView.kt) | Сведения, управляемые комментарий/URI, сохранение изменений; иконка удаления и диалог подтверждения; автоподкрепление видео |

Маршрут: [ScheduleScreen](../ScheduleScreen.kt) → ScheduleAction → ScheduleViewModel → [контракт](../../../domain/repository/PACKAGE.md) → [Room repository](../../../data/repository/PACKAGE.md). UI-модель — [model](model/PACKAGE.md), поля и видео — [component](component/PACKAGE.md).

Создание получает выбранную дату/свободный час, 40 минут, новый UUID сессии, пустые материалы. Невалидный raw ввод остаётся. Details→Edit копирует черновик; отмена возвращает исходные детали. Success создания закрывает форму, edit возвращает обновлённые детали, детали меняют сохранённую основу. Loading/Error не уничтожают ввод. Имена обновляются по ID.

Saving блокирует изменения и уход. После commit подтверждённый агрегат накладывается до равного Ready: новые записи ждут, навигация доступна. Ошибка чтения предлагает чтение, не повтор подтверждённой записи. Activity-scoped ViewModel переживает поворот; смерть процесса сохраняет только БД.

Автоподкрепление видео: кнопка «Найти видео занятия» в разделе видео деталей и редактирования ([component](component/PACKAGE.md)) вызывает `AutoAttachLessonVideos(sessionId)`. Интервал — из `editor.toLesson()` (значения формы); при некорректных дата/время/длительность поиск не выполняется, показывается `videoScanMessage` об исправлении значений. Скан идёт через [FindLessonVideosUseCase](../../../domain/usecase/PACKAGE.md) вне UI-потока; найденные URI без дубликатов попадают в черновик тем же путём, что и выбор из пикера (`ChangeVideos`), применение — только при сохранении. `videoScanInProgress` неактивирует кнопку до конца поиска; результат («Добавлено видео: N» / «Видео за время занятия не найдены») — в `videoScanMessage`, сбрасывается при новом поиске и смене редактора. Сессия и saving после скана перепроверяются.

Удаление — только из деталей: иконка рядом с редактированием (enabled = canDelete и не saving), диалог «Удалить занятие?». canDelete — нет saving/awaitingSnapshot, справочники Ready, запись в снапшоте, режим ≠ CREATE. Перед вызовом редактор переходит в Saving, `confirmed` сбрасывается; при успехе редактор закрывается, при ошибке — `Failure(reason)` с существующими сообщениями. В `refresh()` DETAILS/EDIT с пропавшей записью закрываются автоматически.

WeekTimeline сортирует занятия дня по времени начала и группирует строгие пересечения интервалов: первые два занятия группы — столбики по половине ширины дня, остальные скрыты за одной кнопкой «ещё N» на группу у верхнего края первого скрытого занятия. 90 минут занимают 1,5 часа; полночь разбивает занятие по дням. Двойное нажатие свободного часа передаёт дату/время в StartCreatingLesson. Сетка первоначально показывает 08:00–13:00 и прокручивается по 24 часам. Расчёт не изменён функцией 010.

Настройка «Скрывать выходные» ([data.preferences](../../../data/preferences/PACKAGE.md)): подписка в `init` ScheduleViewModel, флаг `weekendHidden` в ScheduleUiState; WeekScheduleView строит пять столбцов (пн–пт) вместо семи, занятость скрытых дней не рендерится, часть ночного занятия в скрытый день не показывается, подпись диапазона недели — вся неделя.

## Проверки

- [WeekTimelineTest.kt](../../../../../../../test/java/com/logoped_plus/ui/screen/schedule/WeekTimelineTest.kt) — test.
- [LessonSeedTest.kt](../../../../../../../androidTest/java/com/logoped_plus/ui/screen/schedule/LessonSeedTest.kt) — androidTest, сиды демо-данных в application-БД для ручных проверок расписания.

Скрытые выходные проверялись вручную на эмуляторе (сетка 5/7 столбцов, ночное занятие, сохранение после перезапуска).
