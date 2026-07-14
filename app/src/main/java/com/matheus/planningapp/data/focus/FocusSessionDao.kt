package com.matheus.planningapp.data.focus

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FocusSessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(focusSession: FocusSessionEntity)

    @Query("SELECT * FROM focus_sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<FocusSessionEntity>>

    @Query("SELECT * FROM focus_sessions ORDER BY startTime DESC LIMIT :limit OFFSET :offset")
    suspend fun getSessionsPaged(limit: Int, offset: Int): List<FocusSessionEntity>

    @Query("SELECT COUNT(*) FROM focus_sessions")
    suspend fun getSessionCount(): Int
}
