package com.matheus.planningapp.util

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.matheus.planningapp.R
import com.matheus.planningapp.data.commitment.CommitmentEntity

object NotificationConfig {
    const val CHANNEL_ID = "PlanningAppNotificationsId"
    const val CHANNEL_NAME = "Planning App Notifications"
}

object NotificationExtras {
    const val EXTRA_ID = 0
    const val EXTRA_TITLE = "Task notification"
    const val EXTRA_MESSAGE = "You have a task scheduled for now."
}

class NotificationChannelManager(private val context: Context) {
    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            /* TODO: Create a settings file with env variables */
            val channel: NotificationChannel = NotificationChannel(
                NotificationConfig.CHANNEL_ID,
                NotificationConfig.CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel for notifications of scheduled tasks."
            }

            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}

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

class NotificationReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val commitmentId: Int = intent.getIntExtra("id", NotificationExtras.EXTRA_ID)
        val title: String = intent.getStringExtra("title") ?: NotificationExtras.EXTRA_TITLE
        val message: String = intent.getStringExtra("message") ?: NotificationExtras.EXTRA_MESSAGE

        val builder = NotificationCompat.Builder(context, NotificationConfig.CHANNEL_ID)
            .setSmallIcon(R.drawable.outline_notifications_24)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        val manager = NotificationManagerCompat.from(context)

        try {
            manager.notify(commitmentId, builder.build())
        } catch (e: SecurityException) {
            // Notification exception
        }
    }
}

fun Context.hasNotificationPermission(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED)
}

fun AlarmManager.canScheduleExact(): Boolean {
    return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) && (this.canScheduleExactAlarms())
}
