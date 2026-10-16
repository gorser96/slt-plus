package com.logoped_plus.ui.screen.children

import com.logoped_plus.domain.model.Child
import com.logoped_plus.domain.model.ChildSchedule
import com.logoped_plus.domain.model.WeeklyLessonSlot
import java.text.Collator
import java.time.LocalTime
import java.time.LocalDate
import java.util.Locale

private val DAY_TIME = Regex("^\\d{1,2}:\\d{2}$")

fun parseSlotTime(text: String): LocalTime? {
    if (!DAY_TIME.matches(text)) return null
    val (hour, minute) = text.split(":")
    val h = hour.toInt()
    val m = minute.toInt()
    if (h !in 0..23 || m !in 0..59) return null
    return LocalTime.of(h, m)
}

fun parseSlot(slot: SlotDraft): WeeklyLessonSlot? {
    val start = parseSlotTime(slot.startTimeText) ?: return null
    val minutes = slot.durationText.toIntOrNull() ?: return null
    if (minutes <= 0) return null
    return WeeklyLessonSlot(slot.id, slot.dayOfWeek, start, minutes)
}

fun draftSchedule(draft: ChildCardDraft): ChildSchedule? {
    val slots = ArrayList<WeeklyLessonSlot>(draft.slots.size)
    for (slot in draft.slots) {
        val parsed = parseSlot(slot) ?: return null
        slots.add(parsed)
    }
    return ChildSchedule(slots, draft.endDate)
}

fun projectChildren(children: List<Child>, query: String): List<Child> {
    val effective = query.trim()
    val filtered = if (effective.isEmpty()) children
        else children.filter { it.name.contains(effective, ignoreCase = true) }
    val collator = Collator.getInstance(Locale("ru")).apply { strength = Collator.PRIMARY }
    return filtered.mapIndexed { index, child -> child to index }
        .sortedWith(Comparator { a: Pair<Child, Int>, b: Pair<Child, Int> ->
            val byName = collator.compare(a.first.name, b.first.name)
            if (byName != 0) byName else a.second - b.second
        })
        .map { it.first }
}
