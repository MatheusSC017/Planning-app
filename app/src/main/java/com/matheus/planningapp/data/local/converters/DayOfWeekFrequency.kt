package com.matheus.planningapp.data.local.converters

import androidx.room.TypeConverter
import com.matheus.planningapp.util.enums.DayOfWeekEnum

class DayOfWeekFrequency {

    @TypeConverter
    fun fromDayOfWeek(dayOfWeekEnum: DayOfWeekEnum): String = dayOfWeekEnum.name

    @TypeConverter
    fun toDayOfWeek(value: String): DayOfWeekEnum = DayOfWeekEnum.valueOf(value)


}