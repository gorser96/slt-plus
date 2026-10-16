package com.logoped_plus.ui.screen.tags

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.logoped_plus.domain.repository.TagRepository

class TagsViewModelFactory(
    private val tagRepository: TagRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TagsViewModel::class.java)) {
            return TagsViewModel(
                tagRepository = tagRepository
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}
