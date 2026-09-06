package com.logoped_plus.ui.screen.schedule.model

import java.time.LocalDateTime

data class LessonUiModel(
    val id: String,
    val scheduledAt: LocalDateTime,
    val durationMinutes: Int,
    val childNames: String,
    val comment: String,
    val childIds: List<String> = emptyList(),
    val videoUris: List<String> = emptyList()
)
