package com.matheus.planningapp.util.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.matheus.planningapp.data.commitment.CommitmentEntity
import com.matheus.planningapp.datastore.SettingsRepository
import com.matheus.planningapp.util.enums.NotificationEnum
import com.matheus.planningapp.util.enums.PriorityEnum
import kotlinx.coroutines.flow.first

class TaskNotificationScheduler(
    private val context: Context,
    private val settingsRepository: SettingsRepository,
) {
    private suspend fun isPriorityInNotificationOption(priorityEnum: PriorityEnum): Boolean {
        val notificationOption = settingsRepository.notificationOptionFlow.first()
        return (notificationOption == NotificationEnum.ALL_COMMITMENT) ||
            ((notificationOption == NotificationEnum.MEDIUM_AND_HIGH_PRIORITY) && (priorityEnum != PriorityEnum.LOW)) ||
            ((notificationOption == NotificationEnum.ONLY_HIGH_PRIORITY) && (priorityEnum == PriorityEnum.HIGH))
    }

    suspend fun scheduleTaskNotification(commitmentEntity: CommitmentEntity) {
        if (!isPriorityInNotificationOption(commitmentEntity.priorityEnum)) {
            return
        }

        scheduleNotification(commitmentEntity)
    }

    fun scheduleNotification(
        commitmentEntity: CommitmentEntity,
        minutesBeforeCommitment: Int = 0,
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // SDK 33+ (Android 13+) requires a notification permission
        if (!context.hasNotificationPermission()) return

        // SDK 31+ (Android 12+) requires schedule exact alarms permission
        if (!alarmManager.canScheduleExact()) return

        val intent =
            Intent(context, NotificationReceiver::class.java).apply {
                putExtra("id", commitmentEntity.id.toInt())
                putExtra("title", commitmentEntity.title)
                putExtra("message", commitmentEntity.description)
            }

        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                commitmentEntity.id.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        try {
            val notificationTime = commitmentEntity.startDateTime.toEpochMilliseconds() - (minutesBeforeCommitment * 60 * 1000)

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                notificationTime,
                pendingIntent,
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    fun cancelTaskNotification(commitmentEntity: CommitmentEntity) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                commitmentEntity.id.toInt(),
                Intent(context, NotificationReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        pendingIntent.let {
            alarmManager.cancel(it)
            it.cancel()
        }
    }
}
