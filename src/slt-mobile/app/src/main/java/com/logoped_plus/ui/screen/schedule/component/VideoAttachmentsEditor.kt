package com.logoped_plus.ui.screen.schedule.component

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

@Composable
fun VideoAttachmentsEditor(videoUris: List<String>, onChange: (List<String>) -> Unit) {
    var pendingRemoval by rememberSaveable { mutableStateOf<String?>(null) }
    var videoError by rememberSaveable { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val videoPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        val addedUris = mutableListOf<String>()
        var failedCount = 0
        uris.distinct().forEach { uri ->
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                addedUris.add(uri.toString())
            } catch (_: SecurityException) {
                failedCount++
            }
        }
        onChange((videoUris + addedUris).distinct())
        videoError = if (failedCount > 0)
            "Не удалось прикрепить файлов: $failedCount. Остальные видео добавлены." else null
    }

    Text("Видео", style = MaterialTheme.typography.titleMedium)
    videoUris.forEach { uri ->
        key(uri) {
            VideoAttachmentLink(uri)
            TextButton(onClick = { pendingRemoval = uri }) {
                Text("Открепить")
            }
        }
    }
    TextButton(
        onClick = {
            videoError = null
            try {
                videoPicker.launch(arrayOf("video/*"))
            } catch (_: ActivityNotFoundException) {
                videoError = "На устройстве не найдено приложение для выбора видео."
            }
        }
    ) { Text("Выбрать видео") }
    videoError?.let { message ->
        Text(message, color = MaterialTheme.colorScheme.error)
    }
    pendingRemoval?.let { uri ->
        AlertDialog(
            onDismissRequest = { pendingRemoval = null },
            title = { Text("Открепить видео?") },
            text = { Text("Видео будет откреплено от занятия после сохранения. Файл останется на устройстве.") },
            confirmButton = {
                TextButton(onClick = {
                    onChange(videoUris - uri)
                    pendingRemoval = null
                }) { Text("Открепить") }
            },
            dismissButton = {
                TextButton(onClick = { pendingRemoval = null }) { Text("Отмена") }
            }
        )
    }
}