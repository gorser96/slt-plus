package com.logoped_plus.ui.screen.schedule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.logoped_plus.domain.model.Lesson
import java.time.LocalDateTime

@Composable
fun LessonCreateView(
    initialDate: LocalDateTime,
    onBack: () -> Unit,
    onCreate: (Lesson) -> Unit
) {
    var childName by remember {
        mutableStateOf("")
    }

    var time by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
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
                value = initialDate.toString(),
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
                singleLine = true
            )

            OutlinedTextField(
                value = childName,
                onValueChange = {
                    childName = it
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Ребёнок")
                },
                placeholder = {
                    Text("Имя ребёнка")
                },
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                onCreate(
                    Lesson(
                        scheduledAt = initialDate,
                        durationMinutes = 40
                    )
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = time.isNotBlank() && childName.isNotBlank()
        ) {
            Text("Создать занятие")
        }
    }
}