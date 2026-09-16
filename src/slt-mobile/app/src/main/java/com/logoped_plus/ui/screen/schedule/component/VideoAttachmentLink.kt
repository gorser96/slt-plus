package com.logoped_plus.ui.screen.schedule.component

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException
import java.io.IOException

@Composable
fun VideoAttachmentLink(uri: String, enabled: Boolean = true) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val currentEnabled by rememberUpdatedState(enabled)
    var checking by remember(uri) { mutableStateOf(false) }
    var error by remember(uri) { mutableStateOf<String?>(null) }
    val fileName by produceState("Видео", uri, context) {
        value = withContext(Dispatchers.IO) {
            runCatching {
                context.contentResolver.query(Uri.parse(uri), arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
                    val column = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (column >= 0 && cursor.moveToFirst()) cursor.getString(column) else null
                }
            }.getOrNull()?.takeIf { it.isNotBlank() } ?: "Видео"
        }
    }
    TextButton(enabled = enabled && !checking, modifier = Modifier.fillMaxWidth(), onClick = {
        if (currentEnabled && !checking) {
            checking = true
            scope.launch {
                try {
                    error = checkVideoAccess(context, uri)
                    if (error == null && currentEnabled) error = launchVideo(context, uri)
                } finally { checking = false }
            }
        }
    }) { Text("▶ $fileName") }
    error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
}

internal suspend fun checkVideoAccess(context: Context, rawUri: String): String? = withContext(Dispatchers.IO) {
    try {
        val uri = Uri.parse(rawUri)
        require(uri.scheme == "content" || uri.scheme == "file")
        context.contentResolver.openFileDescriptor(uri, "r")?.use { /* Close before ACTION_VIEW. */ }
            ?: throw FileNotFoundException()
        null
    } catch (_: FileNotFoundException) {
        "Видеофайл не найден. Прикрепите файл заново."
    } catch (_: SecurityException) {
        "Нет доступа к видео. Прикрепите файл заново."
    } catch (_: IllegalArgumentException) {
        "Некорректная ссылка на видео. Прикрепите файл заново."
    } catch (_: IOException) {
        "Не удалось прочитать видео. Повторите попытку."
    }
}

internal fun launchVideo(context: Context, rawUri: String, launch: (Intent) -> Unit = context::startActivity): String? = try {
    val uri = Uri.parse(rawUri)
    launch(Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "video/*")
        clipData = ClipData.newRawUri("Видео", uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    })
    null
} catch (_: ActivityNotFoundException) {
    "Не найдено приложение для просмотра видео."
} catch (_: SecurityException) {
    "Нет доступа к видео. Прикрепите файл заново."
} catch (_: IllegalArgumentException) {
    "Не удалось открыть видео. Прикрепите файл заново."
}
