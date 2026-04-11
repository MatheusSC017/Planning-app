package com.matheus.planningapp.data.local.converters

import androidx.room.TypeConverter
import com.matheus.planningapp.util.enums.DayOfWeekEnum

class DayOfWeekFrequency {
    @TypeConverter
    fun fromDayOfWeek(days: List<DayOfWeekEnum>): String = days.joinToString(",") { it.name }

    @TypeConverter
    fun toDayOfWeek(value: String): List<DayOfWeekEnum> {
        if (value.isBlank()) return emptyList()
        return value.split(",").map { DayOfWeekEnum.valueOf(it) }
    }
}
