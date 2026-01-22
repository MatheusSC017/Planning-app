package com.matheus.planningapp.data

import androidx.room.TypeConverter

class PriorityConverters {

    @TypeConverter
    fun fromPriority(priority: Priority): String = priority.name

    @TypeConverter
    fun toPriority(value: String): Priority = Priority.valueOf(value)

}