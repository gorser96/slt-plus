package com.logoped_plus.ui.screen.schedule

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.draw.clipToBounds
import java.time.LocalDateTime
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
    onLessonClick: (LessonUiModel) -> Unit,
    onOpenDay: (LocalDate) -> Unit,
    onEmptyHourDoubleClick: (LocalDateTime) -> Unit,
    modifier: Modifier = Modifier,
    weekendHidden: Boolean = false
) {
    val dayCount = if (weekendHidden) 5 else 7
    val timelines = remember(weekStart, lessons, dayCount) {
        List(dayCount) { buildDayTimeline(weekStart.plusDays(it.toLong()), lessons) }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("‹", style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.clickable(onClick = onPreviousWeek).padding(horizontal = 16.dp))
            Text(formatWeekRange(weekStart, weekStart.plusDays(6)),
                style = MaterialTheme.typography.titleMedium)
            Text("›", style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.clickable(onClick = onNextWeek).padding(horizontal = 16.dp))
        }
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth()) {
            Spacer(Modifier.width(44.dp))
            repeat(dayCount) { index ->
                val date = weekStart.plusDays(index.toLong())
                Column(Modifier.weight(1f).clickable(onClick = { onOpenDay(date) }),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("ru"))
                        .replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.labelMedium, textAlign = TextAlign.Center)
                    Text(date.dayOfMonth.toString(), style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        BoxWithConstraints(Modifier.fillMaxWidth().weight(1f)) {
            val hourHeight = (maxHeight / 5).coerceAtLeast(64.dp)
            val density = LocalDensity.current
            val initialScroll = with(density) { (hourHeight * 8).roundToPx() }
            val hoursState = rememberScrollState(initial = initialScroll)
            Row(Modifier.fillMaxSize().verticalScroll(hoursState)) {
                Column(Modifier.width(44.dp)) {
                    repeat(24) { hour ->
                        Column(Modifier.height(hourHeight), verticalArrangement = Arrangement.SpaceBetween) {
                            Text("%02d:00".format(hour), style = MaterialTheme.typography.labelSmall)
                            Text("%02d:00".format(hour + 1), style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                timelines.forEachIndexed { index, timeline ->
                    val date = weekStart.plusDays(index.toLong())
                    BoxWithConstraints(Modifier.weight(1f).height(hourHeight * 24).clipToBounds()) {
                        val dayWidth = maxWidth
                        Column {
                            repeat(24) { hour ->
                                val emptyHourModifier = if (timeline.isHourEmpty(hour)) {
                                    Modifier.pointerInput(date, hour, onEmptyHourDoubleClick) {
                                        detectTapGestures(onDoubleTap = {
                                            onEmptyHourDoubleClick(date.atTime(hour, 0))
                                        })
                                    }
                                } else Modifier
                                Box(Modifier.fillMaxWidth().height(hourHeight)
                                    .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
                                    .then(emptyHourModifier))
                            }
                        }
                        timeline.spans.forEach { span ->
                            val lanes = span.laneCount ?: 1
                            val laneWidth = dayWidth / lanes
                            WeekLessonBlock(
                                uiModel = span.lesson,
                                onClick = { onLessonClick(span.lesson) },
                                modifier = Modifier
                                    .offset(x = laneWidth * (span.lane ?: 0),
                                        y = hourHeight * (span.startMinute / 60f))
                                    .width(laneWidth)
                                    .height(hourHeight * ((span.endMinute - span.startMinute) / 60f))
                                    .padding(horizontal = 1.dp)
                                    .clipToBounds()
                            )
                        }
                    }
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
