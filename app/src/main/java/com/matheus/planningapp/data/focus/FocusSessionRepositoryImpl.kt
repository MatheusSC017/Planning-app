package com.matheus.planningapp.data.focus

import kotlinx.coroutines.flow.Flow

class FocusSessionRepositoryImpl(private val dao: FocusSessionDao) : FocusSessionRepository {
    override suspend fun insertSession(focusSession: FocusSessionEntity) {
        dao.insert(focusSession)
    }

    override fun getAllSessions(): Flow<List<FocusSessionEntity>> = dao.getAllSessions()
}
