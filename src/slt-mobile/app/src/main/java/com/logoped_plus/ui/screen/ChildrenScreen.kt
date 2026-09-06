package com.logoped_plus.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import com.logoped_plus.domain.model.Child
import com.logoped_plus.ui.screen.children.ChildrenViewModel

@Composable
fun ChildrenScreen(
    viewModel: ChildrenViewModel
) {
    val state by viewModel.uiState.collectAsState()

    var editingChild by remember { mutableStateOf<Child?>(null) }
    var childName by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = state.children,
                key = { it.id }
            ) { child ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            editingChild = child
                            childName = child.name
                            showDialog = true
                        }
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(text = child.name)
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = {
                editingChild = null
                childName = ""
                showDialog = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Добавить ребёнка"
            )
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
            },
            title = {
                Text(
                    if (editingChild == null) {
                        "Новый ребёнок"
                    } else {
                        "Редактирование"
                    }
                )
            },
            text = {
                OutlinedTextField(
                    value = childName,
                    onValueChange = { childName = it },
                    label = {
                        Text("Имя")
                    },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val child = editingChild

                        if (child == null) {
                            viewModel.addChild(childName)
                        } else {
                            viewModel.updateChild(
                                child.copy(name = childName)
                            )
                        }

                        showDialog = false
                    },
                    enabled = childName.isNotBlank()
                ) {
                    Text(
                        if (editingChild == null) {
                            "Добавить"
                        } else {
                            "Сохранить"
                        }
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                    }
                ) {
                    Text("Отмена")
                }
            }
        )
    }
}