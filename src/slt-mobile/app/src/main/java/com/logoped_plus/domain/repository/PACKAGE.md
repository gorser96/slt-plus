# com.logoped_plus.domain.repository

Назначение: интерфейсы доступа к доменным данным. [Общий указатель](../../../../../../../../PACKAGES.md).

| Файл / символ | Контракт |
|---|---|
| [ChildRepository.kt](ChildRepository.kt) | `children: StateFlow<List<Child>>`, `getChildById`, `addChild`, `updateChild` |
| [LessonRepository.kt](LessonRepository.kt) | `getLessons`, `getLessonsByDate`, `getLessonById`, `addLesson`, `updateLesson` |

Типы данных — [domain.model](../model/PACKAGE.md); реализации — [data.repository](../../data/repository/PACKAGE.md). Клиенты: [ChildrenViewModel](../../ui/screen/children/PACKAGE.md), [ScheduleViewModel](../../ui/screen/schedule/PACKAGE.md). Связывание выполняется в [App](../../PACKAGE.md).

Методы синхронные, не `suspend`. Только список детей реактивный; занятия читаются явно после изменений в ViewModel. Контрактов удаления пока нет. При изменении API ищи реализации и все вызовы символа в `app/src`. Прямых тестов интерфейсов нет; существующий тест реализации указан в карте `data.repository`.
