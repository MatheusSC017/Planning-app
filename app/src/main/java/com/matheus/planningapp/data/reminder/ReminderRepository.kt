package com.matheus.planningapp.data.reminder

import kotlinx.coroutines.flow.Flow

interface ReminderRepository {
    suspend fun insert(reminderEntity: ReminderEntity): Long

    suspend fun update(reminderEntity: ReminderEntity)

    suspend fun delete(reminderEntity: ReminderEntity)

    fun getRemindersByCommitment(commitmentId: Long): Flow<List<ReminderEntity>>
}
