# com.logoped_plus.data.media

Доступ к видео устройства через MediaStore. [Общий указатель](../../../../../../PACKAGES.md).

| Файл | Назначение |
|---|---|
| [MediaStoreVideoScanner.kt](MediaStoreVideoScanner.kt) | Реализация [VideoLibrary](../../domain/repository/PACKAGE.md): одна выборка из `EXTERNAL_CONTENT_URI` на IO-потоке; время создания — `DATE_TAKEN` с fallback на `DATE_ADDED`; единицы времени нормализует `mediaStoreTimeToMillis` (контракт — секунды, часть ROM, например OnePlus OxygenOS, пишет `DATE_TAKEN` в миллисекундах); интервал `[from, to)` фильтруется в памяти |

Создаётся в [AppContainer](../../AppContainer.kt) и передаётся в [FindLessonVideosUseCase](../../domain/usecase/PACKAGE.md). Требует разрешение `READ_MEDIA_VIDEO` (манифест), запрашивается в [VideoAttachmentsEditor](../../ui/screen/schedule/component/PACKAGE.md).

## Проверки

- [MediaStoreTimeToMillisTest.kt](../../../../../../test/java/com/logoped_plus/data/media/MediaStoreTimeToMillisTest.kt) — test: нормализация секунд/миллисекунд (регрессия OnePlus PJE110).
- Выборка MediaStore и поведение разрешения — ручная приёмка на устройстве.
