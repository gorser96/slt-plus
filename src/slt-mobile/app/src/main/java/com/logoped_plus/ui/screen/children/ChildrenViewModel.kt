package com.logoped_plus.ui.screen.children

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.logoped_plus.domain.model.Child
import com.logoped_plus.domain.repository.ChildLoadState
import com.logoped_plus.domain.repository.ChildRepository
import com.logoped_plus.domain.repository.ChildWriteResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class ChildEditor(
    val id: String,
    val name: String = "",
    val isNew: Boolean = true,
    val saving: Boolean = false,
    val error: ChildWriteResult.Reason? = null
)

data class ChildrenUiState(
    val loadState: ChildLoadState = ChildLoadState.Loading(),
    val editor: ChildEditor? = null
) {
    val children: List<Child> get() = loadState.children
}

class ChildrenViewModel(private val childRepository: ChildRepository) : ViewModel() {
    private val mutableState = MutableStateFlow(ChildrenUiState(childRepository.state.value))
    val uiState = mutableState.asStateFlow()

    init {
        viewModelScope.launch {
            childRepository.state.collect { load -> mutableState.update { it.copy(loadState = load) } }
        }
    }

    fun openNew() {
        if (childRepository.state.value !is ChildLoadState.Ready || uiState.value.editor != null) return
        mutableState.update { it.copy(editor = ChildEditor(UUID.randomUUID().toString())) }
    }

    fun openEdit(child: Child) {
        if (childRepository.state.value !is ChildLoadState.Ready || uiState.value.editor != null) return
        mutableState.update { it.copy(editor = ChildEditor(child.id, child.name, isNew = false)) }
    }

    fun changeName(name: String) {
        mutableState.update { state ->
            val editor = state.editor
            if (editor == null || editor.saving) state
            else state.copy(editor = editor.copy(name = name, error = null))
        }
    }

    fun cancel() {
        if (uiState.value.editor?.saving != true) mutableState.update { it.copy(editor = null) }
    }

    fun retryLoading() {
        if (uiState.value.editor?.saving != true) childRepository.retryLoading()
    }

    fun save() {
        val editor = uiState.value.editor ?: return
        if (editor.saving || editor.name.isBlank() || childRepository.state.value !is ChildLoadState.Ready) return
        mutableState.update { it.copy(editor = editor.copy(saving = true, error = null)) }
        viewModelScope.launch {
            val child = Child(editor.id, editor.name.trim())
            val result = if (editor.isNew) childRepository.addChild(child) else childRepository.updateChild(child)
            mutableState.update { state ->
                when (result) {
                    ChildWriteResult.Success -> state.copy(editor = null)
                    is ChildWriteResult.Failure -> state.copy(editor = editor.copy(error = result.reason))
                }
            }
        }
    }
}
