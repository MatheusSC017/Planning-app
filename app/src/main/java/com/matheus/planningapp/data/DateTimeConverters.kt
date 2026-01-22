package com.matheus.planningapp.data

import androidx.room.TypeConverter
import java.time.Instant

class DateTimeConverters {

    @TypeConverter
    fun fromInstant(value: Instant?): Long? = value?.toEpochMilli()

    @TypeConverter
    fun toInstant(value: Long?): Instant? = value?.let { Instant.ofEpochMilli(it) }

}