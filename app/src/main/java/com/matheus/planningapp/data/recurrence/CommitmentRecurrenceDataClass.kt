package com.matheus.planningapp.data.recurrence

import com.matheus.planningapp.util.enums.DayOfWeekEnum
import com.matheus.planningapp.util.enums.FrequencyEnum
import kotlinx.datetime.Instant

data class CommitmentRecurrenceDataClass(
    val commitmentId: Long = 0L,
    val recurrenceId: Long = 0L,
    val title: String = "",
    val description: String? = null,
    val startDateTime: Instant = Instant.fromEpochMilliseconds(0),
    val endDateTime: Instant = Instant.fromEpochMilliseconds(0),
    val frequency: FrequencyEnum = FrequencyEnum.DAILY,
    val dayOfWeekList: List<DayOfWeekEnum>,
    val dayOfMonth: Int = 1,
    val interval: Int = 1,
)
