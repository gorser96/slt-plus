package com.logoped_plus.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.logoped_plus.domain.repository.ChildLoadState
import com.logoped_plus.domain.repository.ChildWriteResult
import com.logoped_plus.ui.screen.children.ChildrenViewModel

@Composable
fun ChildrenScreen(viewModel: ChildrenViewModel) {
    val state by viewModel.uiState.collectAsState()
    val ready = state.loadState is ChildLoadState.Ready
    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            ChildrenLoadStatus(state.loadState, viewModel::retryLoading)
            if (ready && state.children.isEmpty()) Text("Детей пока нет")
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.children, key = { it.id }) { child ->
                    Card(Modifier.fillMaxWidth().clickable(enabled = ready) { viewModel.openEdit(child) }) {
                        Text(child.name, Modifier.padding(16.dp))
                    }
                }
            }
        }
        if (ready) FloatingActionButton(
            onClick = viewModel::openNew,
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) { Icon(Icons.Default.Add, contentDescription = "Добавить ребёнка") }
    }
    state.editor?.let { editor ->
        AlertDialog(
            onDismissRequest = viewModel::cancel,
            properties = DialogProperties(
                dismissOnBackPress = !editor.saving,
                dismissOnClickOutside = !editor.saving
            ),
            title = { Text(if (editor.isNew) "Новый ребёнок" else "Редактирование") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = editor.name,
                        onValueChange = viewModel::changeName,
                        label = { Text("Имя") },
                        enabled = !editor.saving,
                        singleLine = true
                    )
                    if (editor.saving) Text("Сохранение…")
                    ChildrenLoadStatus(state.loadState, viewModel::retryLoading, !editor.saving)
                    editor.error?.let { reason ->
                        Text(when (reason) {
                            ChildWriteResult.Reason.NotFound -> "Ребёнок не найден. Закройте форму и повторите загрузку."
                            ChildWriteResult.Reason.Conflict -> "Запись уже существует с другими данными."
                            ChildWriteResult.Reason.InvalidName -> "Укажите имя ребёнка"
                            ChildWriteResult.Reason.NotReady -> "Дождитесь загрузки детей"
                            ChildWriteResult.Reason.StorageUnavailable -> "Не удалось сохранить ребёнка. Повторите попытку"
                        }, color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = viewModel::save, enabled = ready && !editor.saving && editor.name.isNotBlank()) {
                    Text(if (editor.isNew) "Добавить" else "Сохранить")
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::cancel, enabled = !editor.saving) { Text("Отмена") }
            }
        )
    }
}

@Composable
internal fun ChildrenLoadStatus(state: ChildLoadState, onRetry: () -> Unit, retryEnabled: Boolean = true) {
    when (state) {
        is ChildLoadState.Loading -> Text("Загрузка детей…")
        is ChildLoadState.Error -> Column {
            Text("Не удалось загрузить детей", color = MaterialTheme.colorScheme.error)
            TextButton(onClick = onRetry, enabled = retryEnabled) { Text("Повторить загрузку") }
        }
        is ChildLoadState.Ready -> Unit
    }
}
