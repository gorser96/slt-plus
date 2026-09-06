package com.logoped_plus.ui.screen.children

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.logoped_plus.domain.model.Child
import com.logoped_plus.domain.repository.ChildRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class ChildrenUiState(
    val children: List<Child> = emptyList()
)

class ChildrenViewModel(
    private val childRepository: ChildRepository
) : ViewModel() {

    val uiState: StateFlow<ChildrenUiState> =
        childRepository.children
            .map { children ->
                ChildrenUiState(
                    children = children
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ChildrenUiState()
            )

    fun addChild(name: String) {
        val trimmedName = name.trim()

        if (trimmedName.isBlank()) {
            return
        }

        childRepository.addChild(
            Child(
                id = java.util.UUID.randomUUID().toString(),
                name = trimmedName
            )
        )
    }

    fun updateChild(child: Child) {
        val trimmedName = child.name.trim()

        if (trimmedName.isBlank()) {
            return
        }

        childRepository.updateChild(
            child.copy(name = trimmedName)
        )
    }
}