# com.logoped_plus.domain.repository

Контракты данных без Android и Room. [Общий указатель](../../../../../../../../PACKAGES.md).

| Файл | Назначение |
|---|---|
| [ChildRepository.kt](ChildRepository.kt) | StateFlow ChildLoadState, retryLoading, suspend addChild/updateChild |
| [ChildLoadState.kt](ChildLoadState.kt) | Loading/Ready/Error с последним списком детей |
| [ChildWriteResult.kt](ChildWriteResult.kt) | Success после commit либо типизированный отказ |
| [LessonRepository.kt](LessonRepository.kt) | StateFlow LessonLoadState, retryLoading, suspend addLesson/updateLesson/deleteLesson; синхронных чтений нет |
| [LessonLoadState.kt](LessonLoadState.kt) | Loading/Ready/Error с целым предыдущим снимком |
| [LessonWriteResult.kt](LessonWriteResult.kt) | Success(lesson); Failure: NotReady, InvalidData, UnknownChild, NotFound, Conflict, StorageUnavailable |
| [VideoLibrary.kt](VideoLibrary.kt) | suspend findVideos(from, to) — видео устройства в интервале эпохи |

Модели — [domain.model](../model/PACKAGE.md), реализации — [data.repository](../../data/repository/PACKAGE.md) и [data.media](../../data/media/PACKAGE.md) (`VideoLibrary`). Клиенты — [дети](../../ui/screen/children/PACKAGE.md), [расписание](../../ui/screen/schedule/PACKAGE.md) и [use cases](../usecase/PACKAGE.md). retryLoading занятий заменяет подписку из Ready/Error; в Loading игнорируется. Success подтверждает commit, а не последующую эмиссию.

## Проверки

На диске тестов для пакета нет.
