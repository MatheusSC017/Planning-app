package com.matheus.planningapp.data.local.converters

import androidx.room.TypeConverter
import com.matheus.planningapp.util.enums.PriorityEnum

class PriorityConverters {
    @TypeConverter
    fun fromPriority(priorityEnum: PriorityEnum): String = priorityEnum.name

    @TypeConverter
    fun toPriority(value: String): PriorityEnum = PriorityEnum.valueOf(value)
}
