package com.logoped_plus.ui.screen.tags

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.logoped_plus.domain.model.Tag
import com.logoped_plus.domain.model.TagColorKey
import com.logoped_plus.domain.repository.TagLoadState
import com.logoped_plus.domain.repository.TagRepository
import com.logoped_plus.domain.repository.TagWriteResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class TagDraft(
    val id: String,
    val name: String = "",
    val color: TagColorKey = TagColorKey.BLUE,
    val isNew: Boolean = true,
    val saving: Boolean = false,
    val error: TagWriteResult.Reason? = null
)

data class TagsUiState(
    val loadState: TagLoadState = TagLoadState.Loading(),
    val draft: TagDraft? = null,
    val deleteTarget: Tag? = null,
    val deleting: Boolean = false,
    val deleteError: TagWriteResult.Reason? = null
) {
    val tags: List<Tag> get() = loadState.tags
    val busy: Boolean get() = deleting || draft?.saving == true
}

class TagsViewModel(private val tagRepository: TagRepository) : ViewModel() {
    private val mutableState = MutableStateFlow(TagsUiState(tagRepository.state.value))
    val uiState = mutableState.asStateFlow()

    init {
        viewModelScope.launch {
            tagRepository.state.collect { load ->
                mutableState.update { state ->
                    val closed = state.deleteTarget != null && load is TagLoadState.Ready &&
                        load.tags.none { it.id == state.deleteTarget?.id }
                    if (closed) state.copy(loadState = load, deleteTarget = null, deleteError = null,
                        deleting = false, draft = if (state.draft?.id == state.deleteTarget?.id) null else state.draft)
                    else state.copy(loadState = load)
                }
            }
        }
    }

    fun openNew() {
        if (tagRepository.state.value !is TagLoadState.Ready || uiState.value.draft != null) return
        mutableState.update { it.copy(draft = TagDraft(UUID.randomUUID().toString())) }
    }

    fun openEdit(tag: Tag) {
        if (tagRepository.state.value !is TagLoadState.Ready || uiState.value.draft != null) return
        mutableState.update { it.copy(draft = TagDraft(tag.id, tag.name, tag.color, isNew = false)) }
    }

    fun changeName(name: String) {
        mutableState.update { state ->
            val draft = state.draft
            if (draft == null || draft.saving) state else state.copy(draft = draft.copy(name = name, error = null))
        }
    }

    fun changeColor(color: TagColorKey) {
        mutableState.update { state ->
            val draft = state.draft
            if (draft == null || draft.saving) state else state.copy(draft = draft.copy(color = color, error = null))
        }
    }

    fun cancel() {
        if (uiState.value.draft?.saving == true) return
        mutableState.update { it.copy(draft = null) }
    }

    fun retryLoading() {
        if (uiState.value.busy) return
        tagRepository.retryLoading()
    }

    fun save() {
        val draft = uiState.value.draft ?: return
        if (draft.saving || draft.name.isBlank() || tagRepository.state.value !is TagLoadState.Ready) return
        mutableState.update { it.copy(draft = draft.copy(saving = true, error = null)) }
        viewModelScope.launch {
            val result = tagRepository.saveTag(Tag(draft.id, draft.name.trim(), draft.color))
            mutableState.update { state ->
                when (result) {
                    TagWriteResult.Success -> state.copy(draft = null)
                    is TagWriteResult.Failure -> state.copy(draft = draft.copy(saving = false, error = result.reason))
                }
            }
        }
    }

    fun openDeleteConfirm(tag: Tag) {
        if (tagRepository.state.value !is TagLoadState.Ready || uiState.value.busy) return
        mutableState.update { it.copy(deleteTarget = tag, deleteError = null) }
    }

    fun cancelDelete() {
        if (uiState.value.deleting) return
        mutableState.update { it.copy(deleteTarget = null, deleteError = null) }
    }

    fun delete() {
        val tag = uiState.value.deleteTarget ?: return
        if (uiState.value.deleting || tagRepository.state.value !is TagLoadState.Ready) return
        mutableState.update { it.copy(deleting = true, deleteError = null) }
        viewModelScope.launch {
            val result = tagRepository.deleteTag(tag)
            mutableState.update { state ->
                when (result) {
                    TagWriteResult.Success -> state.copy(deleting = false)
                    is TagWriteResult.Failure -> state.copy(deleting = false, deleteError = result.reason)
                }
            }
        }
    }
}
