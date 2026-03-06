package com.matheus.planningapp.util.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.matheus.planningapp.data.commitment.CommitmentEntity
import com.matheus.planningapp.data.local.converters.Priority
import com.matheus.planningapp.datastore.SettingsRepository
import com.matheus.planningapp.viewmodel.setting.NotificationEmailOptions
import kotlinx.coroutines.flow.first

class TaskNotificationScheduler(
    private val context: Context,
    private val settingsRepository: SettingsRepository
) {
    private suspend fun isPriorityInNotificationOption(priority: Priority): Boolean {
        val notificationOption = settingsRepository.notificationOptionFlow.first()
        return (notificationOption == NotificationEmailOptions.ALL_COMMITMENT) ||
                ((notificationOption == NotificationEmailOptions.MEDIUM_AND_HIGH_PRIORITY) && (priority != Priority.LOW)) ||
                ((notificationOption == NotificationEmailOptions.ONLY_HIGH_PRIORITY) && (priority == Priority.HIGH))
    }

    suspend fun scheduleTaskNotification(commitmentEntity: CommitmentEntity) {
        if (!isPriorityInNotificationOption(commitmentEntity.priority)) {
            return
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // SDK 33+ (Android 13+) requires a notification permission
        if (!context.hasNotificationPermission()) return

        // SDK 31+ (Android 12+) requires schedule exact alarms permission
        if (!alarmManager.canScheduleExact()) return

        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("id", commitmentEntity.id.toInt())
            putExtra("title", commitmentEntity.title)
            putExtra("message", commitmentEntity.description)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            commitmentEntity.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                commitmentEntity.startDateTime.toEpochMilliseconds(),
                pendingIntent,
            )

        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    fun cancelTaskNotification(commitmentEntity: CommitmentEntity) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            commitmentEntity.id.toInt(),
            Intent(context, NotificationReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        pendingIntent.let {
            alarmManager.cancel(it)
            it.cancel()
        }
    }
}