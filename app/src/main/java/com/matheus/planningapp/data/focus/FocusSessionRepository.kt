package com.matheus.planningapp.data.focus

import kotlinx.coroutines.flow.Flow

interface FocusSessionRepository {
    suspend fun insertSession(focusSession: FocusSessionEntity)
    fun getAllSessions(): Flow<List<FocusSessionEntity>>
}
