package com.matheus.planningapp.util

import kotlinx.datetime.LocalTime
import java.util.Locale

fun timeToIndex(hours: LocalTime): Int {
    return hours.hour * 2 + (if (hours.minute >= 30) 1 else 0)
}

fun indexToTimeString(index: Int): String {
    return String.format(Locale.US, "%02d:%02d", index / 2, (index % 2) * 30)
}