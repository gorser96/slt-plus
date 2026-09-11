# Модель данных: сохранение детей

Основание: [spec.md](spec.md), FR-001–009. Структуры проектируемые.

## Постоянные данные

БД `children.db`, версия 1, таблица `children`:

| Поле | Тип/ограничение | Назначение |
|---|---|---|
| position | INTEGER PRIMARY KEY AUTOINCREMENT | Порядок добавления; неизменен при переименовании |
| id | TEXT NOT NULL, UNIQUE | UUID ребёнка для связей и ключей UI |
| name | TEXT NOT NULL | Имя после Kotlin trim, не уникально |

Чтение всегда ORDER BY position ASC. Domain Child(id, name) остаётся прежним; position наружу не передаётся. Не сортировать по UUID, имени или времени устройства.

Repository проверяет name.trim().isBlank(); пустое имя отклоняется, внутренние пробелы сохраняются. UI применяет то же правило для кнопки. Одинаковые имена не объединяются.

## Операции и целостность

- Добавление: UUID генерируется при открытии новой формы. Транзакция проверяет id: отсутствует — INSERT с новой position; существует с тем же именем — успешный повтор без вставки; с другим именем — Conflict без изменения строки.
- Переименование: UPDATE только name по id; отсутствие строки — NotFound. Не использовать INSERT OR REPLACE, способный изменить порядок строки.
- Commit — граница успеха. Ошибка до commit откатывает изменение. Ошибка последующего наблюдения списка — отдельная ошибка чтения.
- Прерванная процессом неподтверждённая операция оставляет старое либо новое целое состояние; подтверждённая обязана восстановиться.
- Нет seed, импорта из памяти и fallbackToDestructiveMigration. Экспорт схемы v1 — в app/schemas; дальнейшее изменение требует явной миграции.

## Связи

Lesson.childIds ссылается на Child.id. Таблицы занятий и SQL foreign key нет. ScheduleViewModel проверяет наличие всех выбранных ID в Ready-снимке перед CreateLesson/UpdateLesson. Переименование не меняет участников. InMemoryLessonRepository начинается пустым.

## Состояния

ChildLoadState: Loading(previous), Ready(children), Error(previous, StorageUnavailable). previous — последний успешный список либо пустой до первого чтения; только отображаемый снимок, не резервное хранилище.

Первый запуск: Loading → Ready(emptyList). Сбой: Loading/Ready → Error. Повтор: Error → Loading → Ready/Error. При Loading/Error операции блокируются даже с непустым previous.

Редактор: Closed → Editing(id, name, mode) → Saving → Closed при Success либо Editing с ошибкой при Failure. В Editing доступны ввод и отмена; в Saving ввод, подтверждение и закрытие блокируются. ChildrenViewModel сохраняет форму при пересоздании Activity, но восстановление после смерти процесса не требуется.
