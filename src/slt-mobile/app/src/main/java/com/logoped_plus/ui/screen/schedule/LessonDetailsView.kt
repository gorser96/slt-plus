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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.logoped_plus.ui.screen.schedule.model.LessonUiModel
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun LessonDetailsView(
    uiModel: LessonUiModel,
    onBack: () -> Unit
) {
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
                text = "Занятие",
                style = MaterialTheme.typography.titleLarge
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        LessonDetailsRow(
            title = "Дата",
            value = uiModel.scheduledAt.toLocalDate().format(
                DateTimeFormatter.ofPattern(
                    "dd MMMM yyyy",
                    Locale("ru")
                )
            )
        )

        HorizontalDivider()

        LessonDetailsRow(
            title = "Время",
            value = uiModel.scheduledAt.toLocalTime().toString()
        )

        HorizontalDivider()

        LessonDetailsRow(
            title = "Ребёнок",
            value = uiModel.childNames
        )

        HorizontalDivider()

        LessonDetailsRow(
            title = "Тип",
            value = "Индивидуальное занятие"
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                // Редактирование добавим позже.
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Редактировать")
        }
    }
}

@Composable
private fun LessonDetailsRow(
    title: String,
    value: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}