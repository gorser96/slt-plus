# com.logoped_plus.data.repository

Постоянное локальное хранение детей и полного агрегата занятия. [Общий указатель](../../../../../../../../PACKAGES.md).

| Файл | Назначение |
|---|---|
| [RoomChildRepository.kt](RoomChildRepository.kt) | StateFlow Loading/Ready/Error, повтор чтения и suspend-запись детей |
| [RoomLessonRepository.kt](RoomLessonRepository.kt) | Одна заменяемая подписка DAO; готовность обоих справочников; типизированный результат после commit |

Реализует [контракты](../../domain/repository/PACKAGE.md), использует [Room](../local/PACKAGE.md). Создаётся в [AppContainer](../../PACKAGE.md). InMemoryLessonRepository удалён. CancellationException пробрасывается; ошибка наблюдения после commit не отменяет успех записи. Нет seed, удаления записей, сети и fallback в память.

Debug-сервисы защищены DUMP permission, работают в отдельных процессах только с probe-БД. LessonTransactionProbeService проверяет полный агрегат и контрольные данные. В release сервисов нет.

## Проверки

- [RoomChildRepositoryTest.kt](../../../../../../androidTest/java/com/logoped_plus/data/repository/RoomChildRepositoryTest.kt) — androidTest.
- [RoomChildRepositoryFailureTest.kt](../../../../../../androidTest/java/com/logoped_plus/data/repository/RoomChildRepositoryFailureTest.kt) — androidTest.
- [RoomLessonRepositoryTest.kt](../../../../../../androidTest/java/com/logoped_plus/data/repository/RoomLessonRepositoryTest.kt) — androidTest.
- [RoomLessonRepositoryFailureTest.kt](../../../../../../androidTest/java/com/logoped_plus/data/repository/RoomLessonRepositoryFailureTest.kt) — androidTest.
- [TransactionProbeService.kt](../../../../../../debug/java/com/logoped_plus/data/repository/TransactionProbeService.kt) — debug.
- [LessonTransactionProbeService.kt](../../../../../../debug/java/com/logoped_plus/data/repository/LessonTransactionProbeService.kt) — debug.

Результаты и открытые критерии — [приёмка 010](../../../../../../../../openspec/verification/persist-lessons/validation.md).
