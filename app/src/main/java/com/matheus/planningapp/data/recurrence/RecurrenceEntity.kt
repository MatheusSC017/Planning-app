package com.matheus.planningapp.data.recurrence

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.matheus.planningapp.data.commitment.CommitmentEntity
import com.matheus.planningapp.util.enums.DayOfWeekEnum
import com.matheus.planningapp.util.enums.FrequencyEnum
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

@Entity(
    tableName = "Recurrence",
    foreignKeys = [
        ForeignKey(
            entity = CommitmentEntity::class,
            parentColumns = ["id"],
            childColumns = ["commitment"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
class RecurrenceEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val commitment: Long,
    val frequencyEnum: FrequencyEnum,
    val interval: Int,
    val dayOfWeekEnum: DayOfWeekEnum,
    val dayOfMonth: Int,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant  = Clock.System.now()
)
