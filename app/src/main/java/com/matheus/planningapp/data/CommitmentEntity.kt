package com.matheus.planningapp.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(tableName = "Commitment", foreignKeys = [ForeignKey(entity = CalendarEntity::class,
    parentColumns = ["id"],
    childColumns = ["calendar"],
    onDelete = ForeignKey.CASCADE)]
)
data class CommitmentEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val calendar: Int,
    val title: String,
    val description: String,
    val startDateTime: String,
    val endDateTime: String,
    val priority: String,
    val createdAt: String,
    val updatedAt: String
)
