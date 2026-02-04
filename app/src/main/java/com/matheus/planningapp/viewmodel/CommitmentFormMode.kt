package com.matheus.planningapp.viewmodel

import kotlinx.datetime.Instant

sealed interface CommitmentFormMode {
    data class Create(
        val calendarId: Int,
        val initialInstant: Instant
    ): CommitmentFormMode

    data class Edit(
        val commitmentId: Int
    ): CommitmentFormMode
}