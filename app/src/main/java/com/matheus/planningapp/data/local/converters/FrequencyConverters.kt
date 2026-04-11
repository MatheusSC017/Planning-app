package com.matheus.planningapp.data.local.converters

import androidx.room.TypeConverter
import com.matheus.planningapp.util.enums.FrequencyEnum

class FrequencyConverters {
    @TypeConverter
    fun fromFrequency(frequencyEnum: FrequencyEnum): String = frequencyEnum.name

    @TypeConverter
    fun toFrequency(value: String): FrequencyEnum = FrequencyEnum.valueOf(value)
}
