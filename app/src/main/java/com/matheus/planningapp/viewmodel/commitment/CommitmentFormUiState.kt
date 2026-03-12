package com.matheus.planningapp.viewmodel.commitment

import com.matheus.planningapp.util.enums.PriorityEnum
import com.matheus.planningapp.util.enums.NotificationEnum
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class CommitmentFormUiState(
    val id: Long? = null,
    val calendarId: Long = 0,
    val title: String = "",
    val description: String = "",
    val startInstant: Instant = Clock.System.now(),
    val endInstant: Instant = Clock.System.now(),
    val priorityEnum: PriorityEnum = PriorityEnum.LOW,
    val notificationOption: NotificationEnum = NotificationEnum.NO_SEND,
    val isLoading: Boolean = true
)
