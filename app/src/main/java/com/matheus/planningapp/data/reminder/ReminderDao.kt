package com.matheus.planningapp.data.reminder

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ReminderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminderEntity: ReminderEntity)

    @Update
    suspend fun update(reminderEntity: ReminderEntity)

    @Delete
    suspend fun delete(reminderEntity: ReminderEntity)


    @Query(
        """
        SELECT * FROM Reminder WHERE commitment = :commitmentId
    """)
    suspend fun getRemindersByCommitment(commitmentId: Long): List<ReminderEntity>

}