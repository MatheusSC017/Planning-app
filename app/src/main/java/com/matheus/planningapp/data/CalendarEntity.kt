package com.matheus.planningapp.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(
    tableName = "Calendar",
    indices = [Index(value = ["name"], unique = true)]
)
data class CalendarEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val isDefault: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant
)
