package com.matheus.planningapp.viewmodel.commitment

import com.matheus.planningapp.data.local.converters.Priority
import com.matheus.planningapp.viewmodel.setting.NotificationOptions
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class CommitmentFormUiState(
    val id: Long? = null,
    val calendarId: Long = 0,
    val title: String = "",
    val description: String = "",
    val startInstant: Instant = Clock.System.now(),
    val endInstant: Instant = Clock.System.now(),
    val priority: Priority = Priority.LOW,
    val notificationOption: NotificationOptions = NotificationOptions.NO_SEND,
    val isLoading: Boolean = true
)
