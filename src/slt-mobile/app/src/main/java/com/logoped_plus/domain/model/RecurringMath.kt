package com.logoped_plus.domain.model

object RecurringMath {
    const val NANOS_PER_MINUTE = 60_000_000_000L
    const val NANOS_PER_DAY = 86_400_000_000_000L

    fun toAbsoluteNanos(epochDay: Long, nanoOfDay: Long): Long? = try {
        Math.addExact(Math.multiplyExact(epochDay, NANOS_PER_DAY), nanoOfDay)
    } catch (_: ArithmeticException) {
        null
    }

    fun durationNanos(durationMinutes: Int): Long? = try {
        Math.multiplyExact(durationMinutes.toLong(), NANOS_PER_MINUTE)
    } catch (_: ArithmeticException) {
        null
    }

    fun intervalEnd(startAbsoluteNanos: Long, durationMinutes: Int): Long? {
        val duration = durationNanos(durationMinutes) ?: return null
        return try {
            Math.addExact(startAbsoluteNanos, duration)
        } catch (_: ArithmeticException) {
            null
        }
    }
}
