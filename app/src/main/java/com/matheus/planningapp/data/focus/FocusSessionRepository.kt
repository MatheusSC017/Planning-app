package com.matheus.planningapp.data.focus

import kotlinx.coroutines.flow.Flow

interface FocusSessionRepository {
    suspend fun insertSession(focusSession: FocusSessionEntity)
    fun getAllSessions(): Flow<List<FocusSessionEntity>>
    suspend fun getSessionsPaged(page: Int, pageSize: Int): List<FocusSessionEntity>
    suspend fun getTotalSessionsCount(): Int
}
