package com.logoped_plus.domain.usecase

import com.logoped_plus.domain.model.DeviceVideo
import com.logoped_plus.domain.repository.VideoLibrary
import java.time.LocalDateTime
import java.time.ZoneId

class FindLessonVideosUseCase(
    private val videoLibrary: VideoLibrary
) {
    suspend operator fun invoke(
        start: LocalDateTime,
        durationMinutes: Int,
        existingUris: Set<String>
    ): List<DeviceVideo> {
        val zone = ZoneId.systemDefault()
        val from = start.atZone(zone).toInstant()
        val to = start.plusMinutes(durationMinutes.toLong()).atZone(zone).toInstant()
        return videoLibrary.findVideos(from, to).filter { it.uri !in existingUris }
    }
}
