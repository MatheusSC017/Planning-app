package com.matheus.planningapp.data.local.converters

import androidx.room.TypeConverter
import com.matheus.planningapp.data.local.enums.Priority

class PriorityConverters {

    @TypeConverter
    fun fromPriority(priority: Priority): String = priority.name

    @TypeConverter
    fun toPriority(value: String): Priority = Priority.valueOf(value)

}