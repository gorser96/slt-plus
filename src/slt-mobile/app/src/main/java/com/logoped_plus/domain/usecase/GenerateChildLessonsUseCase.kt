package com.logoped_plus.domain.usecase

import com.logoped_plus.domain.repository.GenerateChildLessonsResult
import com.logoped_plus.domain.repository.LessonRepository
import java.time.Clock
import java.time.ZoneId

class GenerateChildLessonsUseCase(
    private val lessonRepository: LessonRepository,
    private val clock: Clock = Clock.systemDefaultZone()
) {
    suspend operator fun invoke(childId: String): GenerateChildLessonsResult =
        lessonRepository.generateChildLessons(
            childId = childId,
            now = clock.instant().atZone(ZoneId.systemDefault()).toLocalDateTime()
        )
}
