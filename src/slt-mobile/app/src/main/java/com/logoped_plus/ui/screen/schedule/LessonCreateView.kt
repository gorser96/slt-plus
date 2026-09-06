package com.logoped_plus.ui.screen.schedule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.logoped_plus.domain.model.Child
import com.logoped_plus.ui.screen.schedule.component.ChildMultiSelectField
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeParseException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonCreateView(
    initialDate: LocalDateTime,
    children: List<Child>,
    onBack: () -> Unit,
    onCreate: (LocalDateTime, List<String>, Int) -> Unit
) {
    var time by rememberSaveable {
        mutableStateOf(initialDate.toLocalTime().toString().take(5))
    }

    var selectedChildIds by rememberSaveable {
        mutableStateOf(emptyList<String>())
    }

    var durationMinutes by rememberSaveable {
        mutableStateOf("40")
    }

    val parsedTime = try {
        LocalTime.parse(time)
    } catch (_: DateTimeParseException) {
        null
    }

    val duration = durationMinutes.toIntOrNull()
    val isDurationValid = duration != null && duration > 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = onBack
            ) {
                Text("← Назад")
            }

            Text(
                text = "Новое занятие",
                style = MaterialTheme.typography.titleLarge
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = initialDate.toLocalDate().toString(),
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Дата")
                },
                readOnly = true
            )

            OutlinedTextField(
                value = time,
                onValueChange = {
                    time = it
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Время")
                },
                placeholder = {
                    Text("Например, 10:00")
                },
                singleLine = true,
                isError = time.isNotBlank() && parsedTime == null
            )

            OutlinedTextField(
                value = durationMinutes,
                onValueChange = { value ->
                    durationMinutes = value.filter { it.isDigit() }
                },
                label = {
                    Text("Длительность, мин")
                },
                singleLine = true,
                isError = !isDurationValid,
                supportingText = {
                    if (!isDurationValid) {
                        Text("Укажите длительность больше 0 минут")
                    }
                }
            )

            Text(
                text = "Дети",
                style = MaterialTheme.typography.titleMedium
            )

            ChildMultiSelectField(
                children = children,
                selectedChildIds = selectedChildIds.toSet(),
                onSelectionChange = {
                    selectedChildIds = it.toList()
                }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                val lessonTime = parsedTime ?: return@Button

                onCreate(
                    LocalDateTime.of(
                        initialDate.toLocalDate(),
                        lessonTime
                    ),
                    selectedChildIds.toList(),
                    durationMinutes.toIntOrNull() ?: 40
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = parsedTime != null && selectedChildIds.isNotEmpty() && isDurationValid
        ) {
            Text("Создать занятие")
        }
    }
}
