# com.logoped_plus.ui.screen.tags

Назначение: состояние и операции справочника «Тэги». [Общий указатель](../../../../../../../../../PACKAGES.md).

| Файл / символ | Ответственность |
|---|---|
| [TagsViewModel.kt](TagsViewModel.kt) — `TagsUiState`, `TagDraft`, `TagsViewModel` | Наблюдение TagRepository.state; редактор (стабильный UUID черновика, имя, цвет из палитры), сохранение с идемпотентным повтором, удаление с подтверждением (отвязка от детей); `busy` блокирует повтор и уход |
| [TagsViewModelFactory.kt](TagsViewModelFactory.kt) | Передача TagRepository; исключение при неизвестном классе |
| [TagPalette.kt](TagPalette.kt) | Палитра: ключ TagColorKey → Compose-цвет и контрастный цвет подписи (вычисляется в UI) |

Точка входа UI — [TagsScreen.kt](../TagsScreen.kt), см. [экраны](../PACKAGE.md). Контракт — [TagRepository](../../../domain/repository/PACKAGE.md); реализация — [data.repository](../../../data/repository/PACKAGE.md). Фабрику использует [App](../../../PACKAGE.md). Назначениям тэгов в карточке ребёнка служит тот же репозиторий — см. [children](../children/PACKAGE.md).

Черновик хранится в ViewModel и переживает пересоздание Activity; saving блокирует поля, повтор и закрытие диалога; ошибка сохраняет введённые значения с повтором и отменой. Загрузка, пустой справочник и ошибка чтения различаются; повтор загрузки не выполняет запись.

Тестов для пакета нет.
