# com.logoped_plus.ui.screen.children

Назначение: состояние и операции списка детей и карточки ребёнка. [Общий указатель](../../../../../../../../../PACKAGES.md).

| Файл / символ | Ответственность |
|---|---|
| [ChildrenViewModel.kt](ChildrenViewModel.kt) — `ChildrenViewModel` | Наблюдение ChildRepository.state и TagRepository.state (удалённый тэг исключается из черновика); карточка: cardId + observeCard (null → «ребёнок не найден», редактирование/генерация недоступны), режим просмотра/редактирования, полный черновик ChildCardDraft (имя, tagIds, строки, endDate), атомарное сохранение saveCard, генерация generateChildLessons (flag generating, результат/ошибка), поиск query; добавление простым диалогом; удаление с подтверждением; `busy` объединяет все операции |
| [ChildrenUiState.kt](ChildrenUiState.kt) — `ChildrenUiState`, `ChildEditor`, `SlotDraft`, `ChildCardDraft` | Состояние экрана; черновики живут в ViewModel и переживают пересоздание Activity |
| [ChildrenProjection.kt](ChildrenProjection.kt) | Чистые UI-проекции: projectChildren (поиск с trim, вхождение без регистра, Collator ru-RU PRIMARY, тёзки — по исходной позиции), parseSlot (время через общий [parseTimeText](../../components/PACKAGE.md), положительная целая длительность), draftSchedule |
| [ChildCardView.kt](ChildCardView.kt) | Карточка: шапка с «← Назад» (просмотр → список, редактор → просмотр; неактивна при операции) и именем; просмотр (имя, цветные тэги из справочника, строки шаблона, дата окончания, «Создать занятия» со счётчиками и повтором, «Редактировать», удаление) и редактор (имя, выбор тэгов, строки «день/время/минуты» на общих контролах [ui.components](../../components/PACKAGE.md), DatePicker даты окончания, валидация, отмена отбрасывает черновик); Back: диалог удаления → редактор → просмотр |
| [ChildrenViewModelFactory.kt](ChildrenViewModelFactory.kt) | Передача ChildRepository, TagRepository и GenerateChildLessonsUseCase; исключение при неизвестном классе |

Точка входа UI — [ChildrenScreen.kt](../ChildrenScreen.kt), см. [экраны](../PACKAGE.md). Контракт — [domain.repository](../../../domain/repository/PACKAGE.md); реализация — [data.repository](../../../data/repository/PACKAGE.md). Фабрику использует [App](../../../PACKAGE.md).

ChildEditor (добавление) хранится в ViewModel; Saving устанавливается до launch; форма закрывается после Success. Ошибка сохраняет имя и ID. Пустое имя запрещено, крайние пробелы удаляются; retryLoading/retryTags не повторяют запись. Изменения получают также подписчики расписания.

Карточка: возврат из редактора без сохранения отбрасывает черновик; сохранение возвращает в просмотр; уход, повтор и изменение полей блокируются во время записи/генерации; сохранение блокируется при ошибке загрузки тэгов (отдельный повтор чтения); удалённый из справочника тэг не восстанавливается при сохранении. Кнопка «← Назад» в шапке повторяет системный Back: в просмотре закрывает карточку, в редакторе отбрасывает черновик; неактивна во время операции (`busy`). Удаление — из карточки с прежним подтверждением `DeleteChildDialog`; успех закрывает карточку и поток чтения.

Проверки: [ChildrenViewModelCardTagsTest.kt](../../../../../../test/java/com/logoped_plus/ui/screen/children/ChildrenViewModelCardTagsTest.kt) — test, открытая карточка отдаёт name/tagIds и готовый справочник тэгов (Ready). Проектные и планировочные правила проверяются тестами, указанными в картах [domain.usecase](../../../domain/usecase/PACKAGE.md) и [data.local](../../../data/local/PACKAGE.md).
