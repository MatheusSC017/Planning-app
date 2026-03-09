package com.matheus.planningapp.viewmodel.home

import com.matheus.planningapp.data.calendar.CalendarEntity
import com.matheus.planningapp.viewmodel.setting.NotificationOptions
import com.matheus.planningapp.viewmodel.setting.ViewOptions
import java.time.LocalDate

data class HomeUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val calendars: List<CalendarEntity> = emptyList(),
    val viewMode: ViewOptions = ViewOptions.COLUMN,
    val notificationOption: NotificationOptions = NotificationOptions.NO_SEND
)