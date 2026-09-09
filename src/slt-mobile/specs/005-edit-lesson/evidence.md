# Основания: Изменение параметров занятия

Дата анализа: 2026-09-09. Прочитаны текущие исходники; приложение и тесты в рамках документирования не запускались.

## Цепочка реализации

- [ui/screen/ScheduleScreen.kt](../../app/src/main/java/com/logoped_plus/ui/screen/ScheduleScreen.kt)
- [ui/screen/schedule/LessonEditView.kt](../../app/src/main/java/com/logoped_plus/ui/screen/schedule/LessonEditView.kt)
- [ui/screen/schedule/ScheduleViewModel.kt](../../app/src/main/java/com/logoped_plus/ui/screen/schedule/ScheduleViewModel.kt)
- [data/repository/InMemoryLessonRepository.kt](../../app/src/main/java/com/logoped_plus/data/repository/InMemoryLessonRepository.kt)

Файлы перечислены для трассировки сценариев; техническое устройство не является требованием к будущей реализации.

## Проверки

InMemoryLessonRepositoryTest проверяет замену записи, количество, соседние записи и перенос даты. Форму, валидацию и переходы он не проверяет.

- [InMemoryLessonRepositoryTest.kt](../../app/src/test/java/com/logoped_plus/data/repository/InMemoryLessonRepositoryTest.kt)

Сценарии в spec.md можно использовать для ручной приёмки. Наличие теста в репозитории не означает его успешный запуск в этой задаче.

## Ограничения и наблюдения

Дополнительных расхождений в пределах описанных сценариев по чтению кода не установлено. Это не исключает ошибок выполнения и отображения на устройстве.

Хранение данных — в памяти. Для проверки повторного открытия записи достаточно закрыть её детали и открыть снова в том же экземпляре приложения; перезапуск процесса не входит в эту гарантию.
