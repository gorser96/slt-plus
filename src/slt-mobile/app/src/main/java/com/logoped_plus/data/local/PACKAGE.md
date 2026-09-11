# com.logoped_plus.data.local

Локальная БД детей. [Индекс](../../../../../../../../PACKAGES.md).

| Файл | Назначение |
|---|---|
| [ChildEntity.kt](ChildEntity.kt) | UUID, имя и независимый порядок добавления |
| [ChildDao.kt](ChildDao.kt) | Наблюдение, атомарная вставка по UUID, переименование |
| [ChildrenDatabase.kt](ChildrenDatabase.kt) | Room v1, пустая файловая БД без seed и destructive fallback |

Потребитель: [репозитории](../repository/PACKAGE.md). Room-типы остаются в data.
