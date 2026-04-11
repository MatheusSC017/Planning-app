package com.matheus.planningapp.navigation

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class CreateCommitmentPayload(
    val calendarId: Long,
    val datetimeInstant: Instant,
)
