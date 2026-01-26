package com.matheus.planningapp.helper

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

private const val MINUTES_IN_DAY = 24 * 60

fun parseTime(value: String): LocalTime? = runCatching { LocalTime.parse(value) }.getOrNull()

fun snapToHalfHour(time: LocalTime): LocalTime {
    val minute = if (time.minute < 30) 0 else 30
    return LocalTime(time.hour, minute)
}

fun LocalTime.toMinutes(): Int =
    hour * 60 + minute

fun minutesToLocalTime(minutes: Int): LocalTime {
    val normalized = ((minutes % MINUTES_IN_DAY) + MINUTES_IN_DAY) % MINUTES_IN_DAY
    return LocalTime(
        hour = normalized / 60,
        minute = normalized % 60
    )
}

fun LocalTime.step30(delta: Int): LocalTime =
    minutesToLocalTime(toMinutes() + delta * 30)


fun sumInstantWithLocalTime(date: Instant, time: LocalTime): Instant {
    val localDate: LocalDate = date.toLocalDateTime(TimeZone.currentSystemDefault()).date

    val newDate: LocalDateTime = LocalDateTime(
        year = localDate.year,
        monthNumber = localDate.monthNumber,
        dayOfMonth = localDate.dayOfMonth,
        hour = time.hour,
        minute = time.minute,
        second = 0,
        nanosecond = 0
    )
    return newDate.toInstant(TimeZone.currentSystemDefault())
}
