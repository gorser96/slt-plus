package com.logoped_plus.ui.screen.children

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.logoped_plus.data.preferences.SettingsStore
import com.logoped_plus.domain.repository.ChildRepository
import com.logoped_plus.domain.repository.TagRepository
import com.logoped_plus.domain.usecase.GenerateChildLessonsUseCase

class ChildrenViewModelFactory(
    private val childRepository: ChildRepository,
    private val tagRepository: TagRepository,
    private val generateChildLessons: GenerateChildLessonsUseCase,
    private val settings: SettingsStore
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChildrenViewModel::class.java)) {
            return ChildrenViewModel(
                childRepository = childRepository,
                tagRepository = tagRepository,
                generateChildLessons = generateChildLessons,
                settings = settings
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}
