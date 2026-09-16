# com.logoped_plus.domain.repository

Контракты данных без Android и Room. [Общий указатель](../../../../../../../../PACKAGES.md).

| Файл | Назначение |
|---|---|
| [ChildRepository.kt](ChildRepository.kt) | StateFlow ChildLoadState, retryLoading, suspend addChild/updateChild |
| [ChildLoadState.kt](ChildLoadState.kt) | Loading/Ready/Error с последним списком детей |
| [ChildWriteResult.kt](ChildWriteResult.kt) | Success после commit либо типизированный отказ |
| [LessonRepository.kt](LessonRepository.kt) | StateFlow LessonLoadState, retryLoading, suspend addLesson/updateLesson; синхронных чтений нет |
| [LessonLoadState.kt](LessonLoadState.kt) | Loading/Ready/Error с целым предыдущим снимком |
| [LessonWriteResult.kt](LessonWriteResult.kt) | Success(lesson); Failure: NotReady, InvalidData, UnknownChild, NotFound, Conflict, StorageUnavailable |

Модели — [domain.model](../model/PACKAGE.md), реализации — [data.repository](../../data/repository/PACKAGE.md). Клиенты — [дети](../../ui/screen/children/PACKAGE.md) и [расписание](../../ui/screen/schedule/PACKAGE.md). retryLoading занятий заменяет подписку из Ready/Error; в Loading игнорируется. Success подтверждает commit, а не последующую эмиссию.

## Проверки



Результаты и открытые критерии — [приёмка 010](../../../../../../../../openspec/verification/persist-lessons/validation.md).
