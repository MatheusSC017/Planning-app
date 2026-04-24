package com.matheus.planningapp.data.reminder

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminderEntity: ReminderEntity): Long

    @Update
    suspend fun update(reminderEntity: ReminderEntity)

    @Delete
    suspend fun delete(reminderEntity: ReminderEntity)

    @Query(
        """
        SELECT * FROM Reminder WHERE commitment = :commitmentId
    """,
    )
    fun getRemindersByCommitment(commitmentId: Long): Flow<List<ReminderEntity>>
}
