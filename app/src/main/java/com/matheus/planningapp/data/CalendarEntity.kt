package com.matheus.planningapp.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant

@Entity(
    tableName = "Calendar",
    indices = [Index(value = ["name"], unique = true)]
)
data class CalendarEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val isDefault: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant
)
