package com.matheus.planningapp.data.reminder

import kotlinx.coroutines.flow.Flow

class ReminderRepositoryImpl(
    private val reminderDao: ReminderDao,
) : ReminderRepository {
    override suspend fun insert(reminderEntity: ReminderEntity): Long = reminderDao.insert(reminderEntity)

    override suspend fun update(reminderEntity: ReminderEntity) {
        reminderDao.update(reminderEntity)
    }

    override suspend fun delete(reminderEntity: ReminderEntity) {
        reminderDao.delete(reminderEntity)
    }

    override fun getRemindersByCommitment(commitmentId: Long): Flow<List<ReminderEntity>> =
        reminderDao.getRemindersByCommitment(commitmentId)
}
