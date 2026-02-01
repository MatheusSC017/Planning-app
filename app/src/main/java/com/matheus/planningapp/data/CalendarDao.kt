package com.matheus.planningapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CalendarDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(calendarEntity: CalendarEntity)

    @Update
    suspend fun update(calendarEntity: CalendarEntity)

    @Delete
    suspend fun delete(calendarEntity: CalendarEntity)

    @Query("SELECT * FROM Calendar ORDER BY isDefault DESC")
    fun getCalendars(): Flow<List<CalendarEntity>>

    @Query("SELECT COUNT(*) FROM Calendar")
    suspend fun countCalendars(): Int

    @Query("UPDATE Calendar SET isDefault = 0 WHERE isDefault = 1")
    suspend fun setAllDefaultAsFalse()
}
