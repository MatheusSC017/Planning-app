package com.matheus.planningapp.viewmodel.home

import com.matheus.planningapp.data.calendar.CalendarEntity
import com.matheus.planningapp.util.enums.NotificationEnum
import com.matheus.planningapp.util.enums.ViewEnum
import java.time.LocalDate

data class HomeUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val calendars: List<CalendarEntity> = emptyList(),
    val viewMode: ViewEnum = ViewEnum.COLUMN,
    val notificationOption: NotificationEnum = NotificationEnum.NO_SEND
)