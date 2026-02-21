package com.matheus.planningapp.viewmodel.home

import com.matheus.planningapp.data.calendar.CalendarEntity
import java.time.LocalDate

data class HomeUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val calendars: List<CalendarEntity> = emptyList()
)