package com.matheus.planningapp.data.local.converters

import androidx.room.TypeConverter
import kotlinx.datetime.Instant

class DateTimeConverters {
    @TypeConverter
    fun fromInstant(value: Instant?): Long? = value?.toEpochMilliseconds()

    @TypeConverter
    fun toInstant(value: Long?): Instant? = value?.let { Instant.fromEpochMilliseconds(it) }
}
