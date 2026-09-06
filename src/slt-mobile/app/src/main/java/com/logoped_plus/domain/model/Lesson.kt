package com.logoped_plus.domain.model

import java.time.LocalDateTime
import java.util.UUID

data class Lesson(
    val id: String = UUID.randomUUID().toString(),
    val childIds: List<String> = emptyList(),
    val scheduledAt: LocalDateTime,
    val durationMinutes: Int,
    val comment: String = "",
    val videoAttachments: List<VideoAttachment> = emptyList()
)