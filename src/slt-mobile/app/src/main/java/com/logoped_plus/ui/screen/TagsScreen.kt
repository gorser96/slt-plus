package com.logoped_plus.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.logoped_plus.domain.model.Tag
import com.logoped_plus.domain.model.TagColorKey
import com.logoped_plus.domain.repository.TagLoadState
import com.logoped_plus.domain.repository.TagWriteResult
import com.logoped_plus.ui.screen.tags.TagsUiState
import com.logoped_plus.ui.screen.tags.TagsViewModel
import com.logoped_plus.ui.screen.tags.tagLabelColor
import com.logoped_plus.ui.screen.tags.tagPalette

@Composable
fun TagsScreen(viewModel: TagsViewModel) {
    val state by viewModel.uiState.collectAsState()
    val ready = state.loadState is TagLoadState.Ready
    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            TagsLoadStatus(state.loadState, viewModel::retryLoading)
            if (ready && state.tags.isEmpty()) Text("Тэгов пока нет")
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.tags, key = { it.id }) { tag ->
                    Card(Modifier.fillMaxWidth().clickable(enabled = ready) { viewModel.openEdit(tag) }) {
                        Row(Modifier.padding(horizontal = 16.dp).padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                            TagSwatch(tag.color)
                            Spacer(Modifier.width(12.dp))
                            Text(tag.name, Modifier.weight(1f))
                            IconButton(
                                onClick = { viewModel.openDeleteConfirm(tag) },
                                enabled = ready
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Удалить тэг", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
        if (ready) FloatingActionButton(
            onClick = viewModel::openNew,
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) { Icon(Icons.Default.Add, contentDescription = "Добавить тэг") }
    }
    state.draft?.let { draft ->
        val onDelete: (() -> Unit)? = if (draft.isNew) null else {
            { viewModel.openDeleteConfirm(Tag(draft.id, draft.name.trim(), draft.color)) }
        }
        TagEditorDialog(
            draft = draft,
            isNew = draft.isNew,
            error = draft.error,
            saving = draft.saving,
            onNameChange = viewModel::changeName,
            onColorChange = viewModel::changeColor,
            onSave = viewModel::save,
            onCancel = viewModel::cancel,
            onDelete = onDelete
        )
    }
    state.deleteTarget?.let { tag ->
        AlertDialog(
            onDismissRequest = viewModel::cancelDelete,
            properties = DialogProperties(dismissOnBackPress = !state.deleting, dismissOnClickOutside = !state.deleting),
            title = { Text("Удалить тэг?") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("«${tag.name}» будет удалён из справочника и отвязан от детей.")
                    Text("Дети и их занятия сохранятся.")
                    if (state.deleting) Text("Удаление…")
                    state.deleteError?.let { reason ->
                        Text(tagDeleteErrorText(reason), color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = viewModel::delete, enabled = !state.deleting && state.loadState is TagLoadState.Ready) { Text("Удалить") }
            },
            dismissButton = {
                TextButton(onClick = viewModel::cancelDelete, enabled = !state.deleting) { Text("Отмена") }
            }
        )
    }
}

@Composable
internal fun TagSwatch(color: TagColorKey) {
    Box(
        Modifier
            .size(24.dp)
            .background(tagPalette[color]!!, RoundedCornerShape(6.dp))
    )
}

@Composable
internal fun TagChip(tag: Tag, modifier: Modifier = Modifier) {
    Box(
        modifier
            .background(tagPalette[tag.color]!!, RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(tag.name, color = tagLabelColor(tag.color), fontWeight = FontWeight.Medium)
    }
}

@Composable
internal fun SmallTagChip(tag: Tag) {
    Box(
        Modifier
            .widthIn(max = 48.dp)
            .background(tagPalette[tag.color]!!, RoundedCornerShape(8.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            tag.name,
            color = tagLabelColor(tag.color),
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TagEditorDialog(
    draft: com.logoped_plus.ui.screen.tags.TagDraft,
    isNew: Boolean,
    error: TagWriteResult.Reason?,
    saving: Boolean,
    onNameChange: (String) -> Unit,
    onColorChange: (TagColorKey) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    onDelete: (() -> Unit)?
) {
    var paletteMenuOpen by remember { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(dismissOnBackPress = !saving, dismissOnClickOutside = !saving),
        title = { Text(if (isNew) "Новый тэг" else "Редактирование тэга") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = draft.name,
                    onValueChange = onNameChange,
                    label = { Text("Название") },
                    enabled = !saving,
                    singleLine = true
                )
                OutlinedButton(onClick = { paletteMenuOpen = true }, enabled = !saving) {
                    TagSwatch(draft.color)
                    Spacer(Modifier.width(8.dp))
                    Text(draft.color.displayName)
                }
                if (saving) Text("Сохранение…")
                error?.let { reason ->
                    Text(tagSaveErrorText(reason), color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onSave, enabled = !saving && draft.name.isNotBlank()) {
                Text(if (isNew) "Добавить" else "Сохранить")
            }
        },
        dismissButton = {
            Row {
                onDelete?.let { onDelete ->
                    TextButton(
                        onClick = onDelete,
                        enabled = !saving,
                        colors = androidx.compose.material3.ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) { Text("Удалить") }
                }
                TextButton(onClick = onCancel, enabled = !saving) { Text("Отмена") }
            }
        }
    )
    if (paletteMenuOpen) {
        AlertDialog(
            onDismissRequest = { paletteMenuOpen = false },
            title = { Text("Цвет") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    TagColorKey.entries.forEach { color ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onColorChange(color)
                                    paletteMenuOpen = false
                                }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TagSwatch(color)
                            Spacer(Modifier.width(12.dp))
                            Text(color.displayName, Modifier.weight(1f))
                            if (color == draft.color) Icon(Icons.Default.Check, contentDescription = null)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { paletteMenuOpen = false }) { Text("Готово") }
            }
        )
    }
}

@Composable
private fun TagsLoadStatus(state: TagLoadState, onRetry: () -> Unit) {
    when (state) {
        is TagLoadState.Loading -> Text("Загрузка тэгов…")
        is TagLoadState.Error -> Column {
            Text("Не удалось загрузить тэги", color = MaterialTheme.colorScheme.error)
            TextButton(onClick = onRetry) { Text("Повторить загрузку") }
        }
        is TagLoadState.Ready -> Unit
    }
}

internal fun tagSaveErrorText(reason: TagWriteResult.Reason): String = when (reason) {
    TagWriteResult.Reason.InvalidName -> "Укажите название тэга"
    TagWriteResult.Reason.DuplicateName -> "Тэг с таким названием уже есть"
    TagWriteResult.Reason.NotFound -> "Тэг не найден. Обновите данные."
    TagWriteResult.Reason.NotReady -> "Дождитесь загрузки тэгов"
    TagWriteResult.Reason.StorageUnavailable -> "Не удалось сохранить тэг. Повторите попытку"
}

internal fun tagDeleteErrorText(reason: TagWriteResult.Reason): String = when (reason) {
    TagWriteResult.Reason.InvalidName -> "Укажите название тэга"
    TagWriteResult.Reason.DuplicateName -> "Тэг с таким названием уже есть"
    TagWriteResult.Reason.NotFound -> "Тэг не найден. Обновите данные."
    TagWriteResult.Reason.NotReady -> "Дождитесь загрузки тэгов"
    TagWriteResult.Reason.StorageUnavailable -> "Не удалось удалить тэг. Повторите попытку"
}
