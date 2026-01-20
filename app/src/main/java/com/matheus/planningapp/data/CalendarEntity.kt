package com.matheus.planningapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Calendar")
data class CalendarEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val isDefault: Boolean,
    val createdAt: String,
    val updatedAt: String
)
