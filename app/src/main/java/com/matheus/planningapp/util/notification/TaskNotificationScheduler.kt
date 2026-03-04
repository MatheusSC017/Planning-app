package com.matheus.planningapp.util.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.matheus.planningapp.data.commitment.CommitmentEntity

class TaskNotificationScheduler(private val context: Context) {
    fun scheduleTaskNotification(commitmentEntity: CommitmentEntity) {
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

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            commitmentEntity.startDateTime.toEpochMilliseconds(),
            pendingIntent,
        )

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