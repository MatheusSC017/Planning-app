package com.matheus.planningapp.viewmodel

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class CommitmentFormState(
    val id: Int? = null,
    val title: String = "",
    val description: String = "",
    val calendarId: Int = 0,
    val startInstant: Instant = Clock.System.now(),
    val isLoading: Boolean = true
)
