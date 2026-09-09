# Основания: Просмотр расписания и деталей занятия

Дата анализа: 2026-09-09. Прочитаны текущие исходники; приложение и тесты в рамках документирования не запускались.

## Цепочка реализации

- [ui/screen/ScheduleScreen.kt](../../app/src/main/java/com/logoped_plus/ui/screen/ScheduleScreen.kt)
- [ui/screen/schedule/ScheduleViewModel.kt](../../app/src/main/java/com/logoped_plus/ui/screen/schedule/ScheduleViewModel.kt)
- [ui/screen/schedule/MonthScheduleView.kt](../../app/src/main/java/com/logoped_plus/ui/screen/schedule/MonthScheduleView.kt)
- [ui/screen/schedule/WeekScheduleView.kt](../../app/src/main/java/com/logoped_plus/ui/screen/schedule/WeekScheduleView.kt)
- [ui/screen/schedule/WeekTimeline.kt](../../app/src/main/java/com/logoped_plus/ui/screen/schedule/WeekTimeline.kt)
- [ui/screen/schedule/LessonDetailsView.kt](../../app/src/main/java/com/logoped_plus/ui/screen/schedule/LessonDetailsView.kt)
- [ui/screen/schedule/ScheduleComponents.kt](../../app/src/main/java/com/logoped_plus/ui/screen/schedule/ScheduleComponents.kt)

Файлы перечислены для трассировки сценариев; техническое устройство не является требованием к будущей реализации.

## Проверки

WeekTimelineTest проверяет расчёт интервалов, общий час, свободные часы и полночь. Это не тест отображения и нажатий; UI-сценарии не покрыты.

- [WeekTimelineTest.kt](../../app/src/test/java/com/logoped_plus/ui/screen/schedule/WeekTimelineTest.kt)

Сценарии в spec.md можно использовать для ручной приёмки. Наличие теста в репозитории не означает его успешный запуск в этой задаче.

## Ограничения и наблюдения

В месячных карточках и списке «ещё N» тип всегда подписан «Индивидуальное занятие», включая группы. Это наблюдаемая неточность, не требование сохранять ошибку. В деталях тип вычисляется по числу детей.

Хранение данных — в памяти. Для проверки повторного открытия записи достаточно закрыть её детали и открыть снова в том же экземпляре приложения; перезапуск процесса не входит в эту гарантию.
