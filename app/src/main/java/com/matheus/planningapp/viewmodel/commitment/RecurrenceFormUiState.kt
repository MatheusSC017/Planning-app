package com.matheus.planningapp.viewmodel.commitment

import com.matheus.planningapp.util.enums.DayOfWeekEnum
import com.matheus.planningapp.util.enums.FrequencyEnum

data class RecurrenceFormUiState(
    val id: Long? = null,
    val isRecurrenceActive: Boolean = false,
    val frequencyEnum: FrequencyEnum = FrequencyEnum.DAILY,
    val interval: Int = 1,
    val daysOfWeekList: List<DayOfWeekEnum> = emptyList(),
    val dayOfMonth: Int = 1,
)
