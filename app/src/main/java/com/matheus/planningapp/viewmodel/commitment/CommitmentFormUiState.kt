package com.matheus.planningapp.viewmodel.commitment

import com.matheus.planningapp.data.local.converters.Priority
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class CommitmentFormUiState(
    val id: Int? = null,
    val calendarId: Int = 0,
    val title: String = "",
    val description: String = "",
    val startInstant: Instant = Clock.System.now(),
    val endInstant: Instant = Clock.System.now(),
    val priority: Priority = Priority.LOW,
    val isLoading: Boolean = true
)
