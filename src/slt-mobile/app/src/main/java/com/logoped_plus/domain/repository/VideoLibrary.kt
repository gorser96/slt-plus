package com.logoped_plus.domain.repository

import com.logoped_plus.domain.model.DeviceVideo
import java.time.Instant

interface VideoLibrary {
    suspend fun findVideos(from: Instant, to: Instant): List<DeviceVideo>
}
