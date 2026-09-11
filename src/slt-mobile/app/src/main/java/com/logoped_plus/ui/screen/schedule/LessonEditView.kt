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
import com.logoped_plus.ui.screen.schedule.component.VideoAttachmentsEditor
import com.logoped_plus.ui.screen.schedule.model.LessonUiModel
import com.logoped_plus.ui.screen.schedule.component.LessonDateTimeFields
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Composable
fun LessonEditView(
    childrenReady: Boolean = true,
    lesson: LessonUiModel,
    children: List<Child>,
    onBack: () -> Unit,
    onSave: (LocalDateTime, List<String>, Int, String, List<String>) -> Unit
) {
    var dateEpochDay by rememberSaveable(lesson.id) {
        mutableStateOf(lesson.scheduledAt.toLocalDate().toEpochDay())
    }
    var hour by rememberSaveable(lesson.id) {
        mutableStateOf(lesson.scheduledAt.hour.toString().padStart(2, '0'))
    }
    var minute by rememberSaveable(lesson.id) {
        mutableStateOf(lesson.scheduledAt.minute.toString().padStart(2, '0'))
    }
    val isTimeValid = hour.toIntOrNull() in 0..23 && minute.toIntOrNull() in 0..59

    var selectedChildIds by rememberSaveable(lesson.id) {
        mutableStateOf(lesson.childIds)
    }

    var durationMinutes by rememberSaveable(lesson.id) {
        mutableStateOf(lesson.durationMinutes.toString())
    }

    var comment by rememberSaveable(lesson.id) { mutableStateOf(lesson.comment) }
    var videoUris by rememberSaveable(lesson.id) { mutableStateOf(lesson.videoUris) }
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
                text = "Редактирование занятия",
                style = MaterialTheme.typography.titleLarge
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LessonDateTimeFields(
                date = LocalDate.ofEpochDay(dateEpochDay),
                onDateChange = { dateEpochDay = it.toEpochDay() },
                hour = hour,
                onHourChange = { hour = it },
                minute = minute,
                onMinuteChange = { minute = it }
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
                enabled = childrenReady,
                children = children,
                selectedChildIds = selectedChildIds.toSet(),
                onSelectionChange = {
                    selectedChildIds = it.toList()
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = comment,
            onValueChange = { comment = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Комментарий специалиста") },
            minLines = 3
        )
        Spacer(modifier = Modifier.height(16.dp))
        VideoAttachmentsEditor(videoUris = videoUris, onChange = { videoUris = it })
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                onSave(
                    LocalDateTime.of(
                        LocalDate.ofEpochDay(dateEpochDay),
                        LocalTime.of(hour.toInt(), minute.toInt())
                    ),
                    selectedChildIds.toList(),
                    duration!!, comment, videoUris
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = childrenReady && isTimeValid && selectedChildIds.isNotEmpty() && isDurationValid
        ) {
            Text("Сохранить изменения")
        }
    }
}
