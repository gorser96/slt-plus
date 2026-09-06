# com.logoped_plus.data.repository

Назначение: реализации хранения детей и занятий в памяти. [Общий указатель](../../../../../../../../PACKAGES.md).

| Файл / символ | Ответственность |
|---|---|
| [InMemoryChildRepository.kt](InMemoryChildRepository.kt) | Три начальных ребёнка; `MutableStateFlow`, поиск по ID, добавление, замена ребёнка по ID |
| [InMemoryLessonRepository.kt](InMemoryLessonRepository.kt) | Три начальных занятия на 6–7 сентября 2026; список, выборка по дате с сортировкой, поиск, добавление, обновление |

Реализует [domain.repository](../../domain/repository/PACKAGE.md), хранит [domain.model](../../domain/model/PACKAGE.md). Экземпляры создаёт [App](../../PACKAGE.md); потребители — [расписание](../../ui/screen/schedule/PACKAGE.md) и [дети](../../ui/screen/children/PACKAGE.md).

Ограничения: нет диска, БД, сети и удаления записей. `getLessons()` возвращает копию списка; поток изменений есть только у детей. Обновление неизвестного занятия вызывает `require` с `Lesson not found`; обновление неизвестного ребёнка оставляет список без изменений. Валидацию пользовательского ввода ищи также в ViewModel и формах.

Тест того же package: [InMemoryLessonRepositoryTest.kt](../../../../../../test/java/com/logoped_plus/data/repository/InMemoryLessonRepositoryTest.kt) — замена занятия без изменения количества и соседних записей, перенос даты, сохранение и очистка комментария/вложений. Запуск: `.\gradlew.bat :app:testDebugUnitTest --tests com.logoped_plus.data.repository.InMemoryLessonRepositoryTest` из корня проекта. Тестов репозитория детей пока нет.
