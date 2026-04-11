package com.matheus.planningapp.viewmodel.recurrence

import com.matheus.planningapp.data.calendar.CalendarEntity

data class RecurrenceUiState(
    val calendars: List<CalendarEntity> = emptyList(),
)
