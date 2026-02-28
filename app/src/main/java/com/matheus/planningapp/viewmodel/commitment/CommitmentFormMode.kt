package com.matheus.planningapp.viewmodel.commitment

import kotlinx.datetime.Instant

sealed interface CommitmentFormMode {
    data class Create(
        val calendarId: Long,
        val initialInstant: Instant
    ): CommitmentFormMode

    data class Edit(
        val commitmentId: Long
    ): CommitmentFormMode
}