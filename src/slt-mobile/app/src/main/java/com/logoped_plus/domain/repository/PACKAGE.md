# com.logoped_plus.domain.repository

Назначение: интерфейсы доступа к доменным данным. [Общий указатель](../../../../../../../../PACKAGES.md).

| Файл / символ | Контракт |
|---|---|
| [ChildRepository.kt](ChildRepository.kt) | `state: StateFlow<ChildLoadState>`, `retryLoading`, suspend `addChild`/`updateChild` |
| [LessonRepository.kt](LessonRepository.kt) | `getLessons`, `getLessonsByDate`, `getLessonById`, `addLesson`, `updateLesson` |

Типы данных — [domain.model](../model/PACKAGE.md); реализации — [data.repository](../../data/repository/PACKAGE.md). Клиенты: [ChildrenViewModel](../../ui/screen/children/PACKAGE.md), [ScheduleViewModel](../../ui/screen/schedule/PACKAGE.md). Связывание выполняется в [App](../../PACKAGE.md).

Операции записи детей — suspend, состояние загрузки/ошибки — StateFlow. Методы занятий синхронные; занятия читаются явно после изменений в ViewModel. Контрактов удаления пока нет. При изменении API ищи реализации и все вызовы символа в `app/src`. Прямых тестов интерфейсов нет; существующий тест реализации указан в карте `data.repository`.


| Файл | Назначение |
|---|---|
| [ChildLoadState.kt](ChildLoadState.kt) | Loading/Ready/Error; предыдущий список только для просмотра |
| [ChildWriteResult.kt](ChildWriteResult.kt) | Success после commit либо типизированная причина отказа |
