package com.matheus.planningapp.data.focus

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "focus_sessions")
data class FocusSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val startTime: Long,
    val durationMinutes: Int,
    val completed: Boolean
)
