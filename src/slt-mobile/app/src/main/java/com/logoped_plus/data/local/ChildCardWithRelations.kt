package com.logoped_plus.data.local

import androidx.room.Embedded
import androidx.room.Relation
import com.logoped_plus.domain.model.Child
import com.logoped_plus.domain.model.ChildSchedule
import com.logoped_plus.domain.model.WeeklyLessonSlot
import java.time.LocalDate
import java.time.LocalTime

data class ChildCardWithRelations(
    @Embedded val child: ChildEntity,
    @Relation(parentColumn = "id", entityColumn = "childId") val tags: List<ChildTagEntity>,
    @Relation(parentColumn = "id", entityColumn = "childId") val schedule: ChildScheduleEntity?,
    @Relation(parentColumn = "id", entityColumn = "childId") val slots: List<ChildScheduleSlotEntity>
)

fun ChildCardWithRelations.toDomain() = Child(
    id = child.id,
    name = child.name,
    tagIds = tags.map { it.tagId },
    schedule = ChildSchedule(
        slots = slots.sortedBy { it.ordinal }.map {
            WeeklyLessonSlot(it.id, it.dayOfWeek, LocalTime.ofNanoOfDay(it.startNanoOfDay), it.durationMinutes)
        },
        endDate = schedule?.let { LocalDate.ofEpochDay(it.endEpochDay) }
    )
)
