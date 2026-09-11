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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.logoped_plus.ui.screen.schedule.model.LessonUiModel
import com.logoped_plus.ui.screen.schedule.component.VideoAttachmentsEditor
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun LessonDetailsView(
    childrenReady: Boolean = true,
    uiModel: LessonUiModel,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    comment: String,
    videoUris: List<String>,
    onCommentChange: (String) -> Unit,
    onVideosChange: (List<String>) -> Unit,
    onSave: () -> Unit
) {
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
                text = "Занятие",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Редактировать занятие")
            }
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
            value = if (uiModel.childIds.size > 1) "Групповое занятие" else "Индивидуальное занятие"
        )

        HorizontalDivider()
        LessonDetailsRow("Длительность", "${uiModel.durationMinutes} мин")
        HorizontalDivider()
        OutlinedTextField(
            value = comment,
            onValueChange = onCommentChange,
            label = { Text("Комментарий специалиста") },
            minLines = 3,
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
        )
        VideoAttachmentsEditor(videoUris, onVideosChange)

        Spacer(modifier = Modifier.height(32.dp))

        if (comment != uiModel.comment || videoUris.toSet() != uiModel.videoUris.toSet()) {
            Button(
                enabled = childrenReady,
                onClick = onSave,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Сохранить")
            }
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
