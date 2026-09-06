package com.logoped_plus.ui.screen.schedule.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.time.LocalDate

@Composable
fun LessonDateTimeFields(
    date: LocalDate,
    onDateChange: (LocalDate) -> Unit,
    hour: String,
    onHourChange: (String) -> Unit,
    minute: String,
    onMinuteChange: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val hourValid = hour.toIntOrNull() in 0..23
    val minuteValid = minute.toIntOrNull() in 0..59

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            LessonDateField(date, onDateChange, Modifier.weight(1f))
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = hour,
                    onValueChange = { value ->
                        if (value.length <= 2 && value.all { it in '0'..'9' }) onHourChange(value)
                    },
                    modifier = Modifier.width(64.dp),
                    label = { Text("Часы") },
                    singleLine = true,
                    isError = !hourValid,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Next) })
                )
                Text(":")
                OutlinedTextField(
                    value = minute,
                    onValueChange = { value ->
                        if (value.length <= 2 && value.all { it in '0'..'9' }) onMinuteChange(value)
                    },
                    modifier = Modifier.width(64.dp),
                    label = { Text("Мин") },
                    singleLine = true,
                    isError = !minuteValid,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                )
            }
        }
        if (!hourValid || !minuteValid) {
            Text(
                "Укажите часы от 0 до 23 и минуты от 0 до 59",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
