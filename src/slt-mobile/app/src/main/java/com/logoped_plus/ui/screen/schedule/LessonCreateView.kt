package com.logoped_plus.ui.screen.schedule

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.logoped_plus.domain.model.Child
import com.logoped_plus.ui.screen.schedule.component.*
import java.time.LocalDate

@Composable
fun LessonCreateView(editor: LessonEditorState, children: List<Child>, childrenReady: Boolean, canSave: Boolean, onAction: (ScheduleAction) -> Unit) {
    LessonForm(editor, children, childrenReady, canSave, onAction)
}

@Composable
internal fun LessonForm(editor: LessonEditorState, children: List<Child>, childrenReady: Boolean, canSave: Boolean, onAction: (ScheduleAction) -> Unit) {
    val creating = editor.mode == LessonEditorMode.CREATE
    val enabled = !editor.saving
    fun text(field: LessonTextField, value: String) { if (enabled) onAction(ScheduleAction.ChangeText(editor.sessionId, field, value)) }
    Column(Modifier.fillMaxSize().imePadding().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        TextButton(enabled = enabled, onClick = { onAction(if (creating) ScheduleAction.CancelCreatingLesson else ScheduleAction.CancelEditingLesson) }) { Text("← Назад") }
        Text(if (creating) "Новое занятие" else "Редактирование занятия", style = MaterialTheme.typography.titleLarge)
        LessonDateTimeFields(
            date = LocalDate.ofEpochDay(editor.epochDay), onDateChange = { onAction(ScheduleAction.ChangeDate(editor.sessionId, it)) },
            hour = editor.hour, onHourChange = { text(LessonTextField.HOUR, it) },
            minute = editor.minute, onMinuteChange = { text(LessonTextField.MINUTE, it) }, enabled = enabled
        )
        OutlinedTextField(value = editor.duration, onValueChange = { text(LessonTextField.DURATION, it) },
            enabled = enabled, label = { Text("Длительность, мин") }, singleLine = true,
            isError = editor.duration.toIntOrNull()?.let { it > 0 } != true,
            supportingText = { if (editor.duration.toIntOrNull()?.let { it > 0 } != true) Text("Укажите длительность больше 0 минут") })
        Text("Дети", style = MaterialTheme.typography.titleMedium)
        ChildMultiSelectField(children = children, selectedChildIds = editor.childIds.toSet(), enabled = enabled && childrenReady,
            onSelectionChange = { onAction(ScheduleAction.ChangeChildren(editor.sessionId, it.toList())) })
        if (!creating) {
            OutlinedTextField(value = editor.comment, onValueChange = { text(LessonTextField.COMMENT, it) }, enabled = enabled,
                label = { Text("Комментарий специалиста") }, minLines = 3, modifier = Modifier.fillMaxWidth())
            VideoAttachmentsEditor(editor.videoUris, { onAction(ScheduleAction.ChangeVideos(editor.sessionId, it)) }, enabled, editor.sessionId)
        }
        Button(enabled = canSave, onClick = { onAction(ScheduleAction.ConfirmLesson(editor.sessionId)) }, modifier = Modifier.fillMaxWidth()) {
            Text(if (editor.saving) "Сохранение…" else if (creating) "Создать занятие" else "Сохранить изменения")
        }
    }
}
