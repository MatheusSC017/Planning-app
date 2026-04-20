package com.matheus.planningapp.data.reminder

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.matheus.planningapp.data.commitment.CommitmentEntity
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

@Entity(
    tableName = "Reminder",
    foreignKeys = [
        ForeignKey(
            entity = CommitmentEntity::class,
            parentColumns = ["id"],
            childColumns = ["commitment"],
            onDelete = ForeignKey.CASCADE
        ),
    ],
    indices = [
        Index("commitment"),
    ],
)
class ReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val commitment: Long,
    val minutesBeforeCommitment: Int,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now(),
)