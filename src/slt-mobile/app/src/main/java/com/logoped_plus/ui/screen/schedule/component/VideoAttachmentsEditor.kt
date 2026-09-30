package com.logoped_plus.ui.screen.schedule.component

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

@Composable
fun VideoAttachmentsEditor(
    videoUris: List<String>,
    onChange: (List<String>) -> Unit,
    enabled: Boolean = true,
    sessionId: String = "",
    onAutoAttach: (() -> Unit)? = null,
    scanInProgress: Boolean = false,
    scanMessage: String? = null
) {
    var pendingRemoval by rememberSaveable { mutableStateOf<String?>(null) }
    var videoError by rememberSaveable { mutableStateOf<String?>(null) }
    var launchedSession by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val videoPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        if (!enabled || launchedSession != sessionId) return@rememberLauncherForActivityResult
        launchedSession = null
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
    val hasVideoPermission = context.checkSelfPermission(Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED
    val videoPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (!enabled || launchedSession != sessionId) return@rememberLauncherForActivityResult
        launchedSession = null
        if (granted) onAutoAttach?.invoke()
        else videoError = "Нет разрешения на доступ к видео устройства."
    }

    Text("Видео", style = MaterialTheme.typography.titleMedium)
    videoUris.forEach { uri ->
        key(uri) {
            VideoAttachmentLink(uri, enabled = enabled)
            TextButton(enabled = enabled, onClick = { pendingRemoval = uri }) {
                Text("Открепить")
            }
        }
    }
    TextButton(
        enabled = enabled,
        onClick = {
            videoError = null
            try {
                launchedSession = sessionId
                videoPicker.launch(arrayOf("video/*"))
            } catch (_: ActivityNotFoundException) {
                videoError = "На устройстве не найдено приложение для выбора видео."
            }
        }
    ) { Text("Выбрать видео") }
    onAutoAttach?.let { attach ->
        TextButton(
            enabled = enabled && !scanInProgress,
            onClick = {
                videoError = null
                if (hasVideoPermission) attach()
                else {
                    launchedSession = sessionId
                    videoPermission.launch(Manifest.permission.READ_MEDIA_VIDEO)
                }
            }
        ) { Text(if (scanInProgress) "Поиск видео…" else "Найти видео занятия") }
    }
    videoError?.let { message ->
        Text(message, color = MaterialTheme.colorScheme.error)
    }
    scanMessage?.let { message ->
        Text(message, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
    pendingRemoval?.takeIf { enabled }?.let { uri ->
        AlertDialog(
            onDismissRequest = { pendingRemoval = null },
            title = { Text("Открепить видео?") },
            text = { Text("Видео будет откреплено от занятия после сохранения. Файл останется на устройстве.") },
            confirmButton = {
                TextButton(enabled = enabled, onClick = {
                    if (enabled) onChange(videoUris - uri)
                    pendingRemoval = null
                }) { Text("Открепить") }
            },
            dismissButton = {
                TextButton(enabled = enabled, onClick = { pendingRemoval = null }) { Text("Отмена") }
            }
        )
    }
}
