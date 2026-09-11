# com.logoped_plus.ui.screen.children

Назначение: состояние и операции списка детей. [Общий указатель](../../../../../../../../../PACKAGES.md).

| Файл / символ | Ответственность |
|---|---|
| [ChildrenViewModel.kt](ChildrenViewModel.kt) — `ChildrenUiState`, `ChildrenViewModel` | Наблюдение ChildRepository.state; редактор ChildEditor, добавление и обновление после commit |
| [ChildrenViewModelFactory.kt](ChildrenViewModelFactory.kt) | Передача `ChildRepository` в ViewModel; исключение при неизвестном классе |

Точка входа UI — [ChildrenScreen.kt](../ChildrenScreen.kt), см. [экраны](../PACKAGE.md). Контракт — [domain.repository](../../../domain/repository/PACKAGE.md); реализация — [data.repository](../../../data/repository/PACKAGE.md). Фабрику использует [App](../../../PACKAGE.md).

ChildEditor хранится в ViewModel и переживает пересоздание Activity. Saving устанавливается до launch; форма закрывается после Success. Ошибка сохраняет имя и ID. Пустое имя запрещено, крайние пробелы удаляются; retryLoading не повторяет запись. Изменения получают также подписчики расписания.

Тест: [ChildrenViewModelTest.kt](../../../../../../../test/java/com/logoped_plus/ui/screen/children/ChildrenViewModelTest.kt) — ввод, отмена, повтор, ожидание commit, переименование.
