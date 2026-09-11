# com.logoped_plus.data.repository

Назначение: постоянное хранение детей и хранение занятий в памяти. [Общий указатель](../../../../../../../../PACKAGES.md).

| Файл / символ | Ответственность |
|---|---|
| [RoomChildRepository.kt](RoomChildRepository.kt) | Общий StateFlow Loading/Ready/Error, повтор чтения, suspend-запись через DAO |
| [InMemoryLessonRepository.kt](InMemoryLessonRepository.kt) | Пустой начальный список, выборка по дате с сортировкой, поиск, добавление, обновление |

Реализует [domain.repository](../../domain/repository/PACKAGE.md), хранит [domain.model](../../domain/model/PACKAGE.md). Экземпляры создаёт [App](../../PACKAGE.md); потребители — [расписание](../../ui/screen/schedule/PACKAGE.md) и [дети](../../ui/screen/children/PACKAGE.md).

Ограничения: нет сети и удаления записей; занятия не сохраняются на диск. `getLessons()` возвращает копию списка; поток изменений есть только у детей. Обновление неизвестного занятия вызывает `require` с `Lesson not found`; обновление неизвестного ребёнка возвращает NotFound. Валидацию пользовательского ввода ищи также в ViewModel и формах.

Тест того же package: [InMemoryLessonRepositoryTest.kt](../../../../../../test/java/com/logoped_plus/data/repository/InMemoryLessonRepositoryTest.kt) — замена занятия без изменения количества и соседних записей, перенос даты, сохранение и очистка комментария/вложений. Запуск: `.\gradlew.bat :app:testDebugUnitTest --tests com.logoped_plus.data.repository.InMemoryLessonRepositoryTest` из корня проекта. Тесты детей перечислены ниже.


Тесты Android: [RoomChildRepositoryTest.kt](../../../../../../androidTest/java/com/logoped_plus/data/repository/RoomChildRepositoryTest.kt) — файловое восстановление, тёзки и порядок; [RoomChildRepositoryFailureTest.kt](../../../../../../androidTest/java/com/logoped_plus/data/repository/RoomChildRepositoryFailureTest.kt) — ошибки и повтор.

Только debug: [TransactionProbeService.kt](../../../../../../debug/java/com/logoped_plus/data/repository/TransactionProbeService.kt) — стенд транзакций в отдельном процессе, доступен ADB через DUMP permission, использует только probe-файлы. В release отсутствует.
