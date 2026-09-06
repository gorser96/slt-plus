# com.logoped_plus.ui.screen.children

Назначение: состояние и операции списка детей. [Общий указатель](../../../../../../../../../PACKAGES.md).

| Файл / символ | Ответственность |
|---|---|
| [ChildrenViewModel.kt](ChildrenViewModel.kt) — `ChildrenUiState`, `ChildrenViewModel` | Преобразование `ChildRepository.children` в UI-состояние через `stateIn`; добавление и обновление имени |
| [ChildrenViewModelFactory.kt](ChildrenViewModelFactory.kt) | Передача `ChildRepository` в ViewModel; исключение при неизвестном классе |

Точка входа UI — [ChildrenScreen.kt](../ChildrenScreen.kt), см. [экраны](../PACKAGE.md). Контракт — [domain.repository](../../../domain/repository/PACKAGE.md); реализация — [data.repository](../../../data/repository/PACKAGE.md). Фабрику использует [App](../../../PACKAGE.md).

`addChild` и `updateChild` обрезают пробелы и игнорируют пустое имя; при добавлении создаётся UUID. Подписка использует `SharingStarted.WhileSubscribed(5_000)` с начальным пустым списком. Диалог и его поля находятся в экране, отдельного класса действий нет. Изменения имён также получает [ScheduleViewModel](../schedule/PACKAGE.md) через общий repository. Специализированных тестов ViewModel пока нет.
