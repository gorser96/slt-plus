package com.logoped_plus.ui.screen.schedule

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.logoped_plus.domain.model.Lesson
import com.logoped_plus.ui.screen.schedule.model.LessonUiModel
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun WeekScheduleView(
    weekStart: LocalDate,
    lessons: List<LessonUiModel>,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit,
    onLessonClick: (LessonUiModel) -> Unit
) {
    val weekEnd = weekStart.plusDays(6)

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "‹",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .clickable(onClick = onPreviousWeek)
                    .padding(horizontal = 16.dp)
            )

            Text(
                text = formatWeekRange(
                    weekStart,
                    weekEnd
                ),
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "›",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .clickable(onClick = onNextWeek)
                    .padding(horizontal = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            repeat(7) { index ->
                val date = weekStart.plusDays(index.toLong())

                WeekDayColumn(
                    date = date,
                    lessons = lessons.filter { it.scheduledAt.toLocalDate() == date },
                    onLessonClick = onLessonClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun WeekDayColumn(
    date: LocalDate,
    lessons: List<LessonUiModel>,
    onLessonClick: (LessonUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = date.dayOfWeek
                .getDisplayName(
                    TextStyle.SHORT,
                    Locale("ru")
                )
                .replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center
        )

        Text(
            text = date.dayOfMonth.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (lessons.isEmpty()) {
            Text(
                text = "—",
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                lessons
                    .sortedBy { it.scheduledAt.toLocalTime() }
                    .forEach { lesson ->
                        WeekLessonItem(
                            uiModel = lesson,
                            onClick = {
                                onLessonClick(lesson)
                            }
                        )
                    }
            }
        }
    }
}

private fun formatWeekRange(
    start: LocalDate,
    end: LocalDate
): String {
    val locale = Locale("ru")

    val startMonth = start.month.getDisplayName(
        TextStyle.SHORT,
        locale
    )

    val endMonth = end.month.getDisplayName(
        TextStyle.SHORT,
        locale
    )

    return if (start.month == end.month) {
        "${start.dayOfMonth}–${end.dayOfMonth} $endMonth ${end.year}"
    } else {
        "${start.dayOfMonth} $startMonth – ${end.dayOfMonth} $endMonth"
    }
}