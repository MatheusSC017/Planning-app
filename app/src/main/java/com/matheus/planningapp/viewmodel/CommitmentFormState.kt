package com.matheus.planningapp.viewmodel

import com.matheus.planningapp.data.Priority
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class CommitmentFormState(
    val id: Int? = null,
    val calendarId: Int = 0,
    val title: String = "",
    val description: String = "",
    val startInstant: Instant = Clock.System.now(),
    val endInstant: Instant = Clock.System.now(),
    val priority: Priority = Priority.LOW,
    val isLoading: Boolean = true
)
