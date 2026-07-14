package com.matheus.planningapp.data.focus

import kotlinx.coroutines.flow.Flow

class FocusSessionRepositoryImpl(private val dao: FocusSessionDao) : FocusSessionRepository {
    override suspend fun insertSession(focusSession: FocusSessionEntity) {
        dao.insert(focusSession)
    }

    override fun getAllSessions(): Flow<List<FocusSessionEntity>> = dao.getAllSessions()

    override suspend fun getSessionsPaged(page: Int, pageSize: Int): List<FocusSessionEntity> {
        val offset = page * pageSize
        return dao.getSessionsPaged(pageSize, offset)
    }

    override suspend fun getTotalSessionsCount(): Int = dao.getSessionCount()
}
