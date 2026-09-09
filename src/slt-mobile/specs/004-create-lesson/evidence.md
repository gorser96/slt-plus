# Основания: Создание занятия

Дата анализа: 2026-09-09. Прочитаны текущие исходники; приложение и тесты в рамках документирования не запускались.

## Цепочка реализации

- [ui/screen/ScheduleScreen.kt](../../app/src/main/java/com/logoped_plus/ui/screen/ScheduleScreen.kt)
- [ui/screen/schedule/LessonCreateView.kt](../../app/src/main/java/com/logoped_plus/ui/screen/schedule/LessonCreateView.kt)
- [ui/screen/schedule/ScheduleViewModel.kt](../../app/src/main/java/com/logoped_plus/ui/screen/schedule/ScheduleViewModel.kt)
- [ui/screen/schedule/WeekScheduleView.kt](../../app/src/main/java/com/logoped_plus/ui/screen/schedule/WeekScheduleView.kt)
- [ui/screen/schedule/component/ChildMultiSelectField.kt](../../app/src/main/java/com/logoped_plus/ui/screen/schedule/component/ChildMultiSelectField.kt)
- [ui/screen/schedule/component/LessonDateField.kt](../../app/src/main/java/com/logoped_plus/ui/screen/schedule/component/LessonDateField.kt)
- [ui/screen/schedule/component/LessonDateTimeFields.kt](../../app/src/main/java/com/logoped_plus/ui/screen/schedule/component/LessonDateTimeFields.kt)
- [data/repository/InMemoryLessonRepository.kt](../../app/src/main/java/com/logoped_plus/data/repository/InMemoryLessonRepository.kt)

Файлы перечислены для трассировки сценариев; техническое устройство не является требованием к будущей реализации.

## Проверки

Прямых тестов формы и создания нет. WeekTimelineTest частично проверяет определение свободного часа.

- [WeekTimelineTest.kt](../../app/src/test/java/com/logoped_plus/ui/screen/schedule/WeekTimelineTest.kt)

Сценарии в spec.md можно использовать для ручной приёмки. Наличие теста в репозитории не означает его успешный запуск в этой задаче.

## Ограничения и наблюдения

Дополнительных расхождений в пределах описанных сценариев по чтению кода не установлено. Это не исключает ошибок выполнения и отображения на устройстве.

Хранение данных — в памяти. Для проверки повторного открытия записи достаточно закрыть её детали и открыть снова в том же экземпляре приложения; перезапуск процесса не входит в эту гарантию.
