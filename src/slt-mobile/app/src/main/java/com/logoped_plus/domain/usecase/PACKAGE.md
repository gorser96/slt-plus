# com.logoped_plus.domain.usecase

Бизнес-правила без Android. [Общий указатель](../../../../../../../../PACKAGES.md).

| Файл | Назначение |
|---|---|
| [FindLessonVideosUseCase.kt](FindLessonVideosUseCase.kt) | Интервал занятия `[start, start+duration)` в эпохе через зону устройства; видео из [VideoLibrary](../repository/PACKAGE.md) без URI, уже находящихся в черновике |

Зависит только от [domain.model](../model/PACKAGE.md) и [domain.repository](../repository/PACKAGE.md). Создаётся в [AppContainer](../../AppContainer.kt), потребляет [ScheduleViewModel](../../ui/screen/schedule/PACKAGE.md).

## Проверки

- [FindLessonVideosUseCaseTest.kt](../../../../../../test/java/com/logoped_plus/domain/usecase/FindLessonVideosUseCaseTest.kt) — test: границы интервала (старт включительно, конец не включительно), дубликаты из черновика, пустой результат, занятие через полночь.
