package com.logoped_plus.ui.screen.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.logoped_plus.data.preferences.SettingsStore
import com.logoped_plus.domain.repository.ChildRepository
import com.logoped_plus.domain.repository.LessonRepository
import com.logoped_plus.domain.usecase.FindLessonVideosUseCase

class ScheduleViewModelFactory(
    private val lessonRepository: LessonRepository,
    private val childRepository: ChildRepository,
    private val settings: SettingsStore,
    private val findLessonVideos: FindLessonVideosUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScheduleViewModel::class.java)) {
            return ScheduleViewModel(lessonRepository, childRepository, settings, findLessonVideos) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}