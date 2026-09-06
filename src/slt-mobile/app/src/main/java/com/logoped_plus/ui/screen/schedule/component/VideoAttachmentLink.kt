package com.logoped_plus.ui.screen.schedule.component

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun VideoAttachmentLink(uri: String) {
    val context = LocalContext.current
    var error by remember(uri) { mutableStateOf<String?>(null) }
    val fileName by produceState("Видео", uri, context) {
        value = withContext(Dispatchers.IO) {
            runCatching {
                context.contentResolver.query(
                    Uri.parse(uri), arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null
                )?.use { cursor ->
                    val column = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (column >= 0 && cursor.moveToFirst()) cursor.getString(column) else null
                }
            }.getOrNull()?.takeIf { it.isNotBlank() } ?: "Видео"
        }
    }

    TextButton(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            error = null
            try {
                val videoUri = Uri.parse(uri)
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(videoUri, "video/*")
                    clipData = ClipData.newRawUri("Видео", videoUri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(intent)
            } catch (_: ActivityNotFoundException) {
                error = "Не найдено приложение для просмотра видео."
            } catch (_: SecurityException) {
                error = "Нет доступа к видео. Прикрепите файл заново."
            } catch (_: IllegalArgumentException) {
                error = "Не удалось открыть видео. Прикрепите файл заново."
            }
        }
    ) { Text("▶ $fileName") }
    error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
}
