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

const val CHANNEL_ID = "PlanningAppNotificationsId"
const val CHANNEL_NAME = "Planning App Notifications"


/* TODO: Put NotificationHelper in DI */
class NotificationHelper(private val context: Context) {
    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            /* TODO: Create a settings file with env variables */
            val channel: NotificationChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel for notifications of scheduled tasks."
            }

            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    fun scheduleTaskNotification(commitmentEntity: CommitmentEntity) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // SDK 33+ (Android 13+) requires a notification permission
        if (checkNotificationPermission(context)) return

        // SDK 31+ (Android 12+) requires schedule exact alarms permission
        if (checkScheduleExactAlarmPermission(alarmManager)) return

        val intent = Intent(context, NotificationReceiver::class.java).apply {
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
}

class NotificationReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title: String = intent.getStringExtra("title") ?: "Task notification"
        val message: String = intent.getStringExtra("message") ?: "You have a task scheduled for now."

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.outline_notifications_24)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        val manager = NotificationManagerCompat.from(context)

        try {
            manager.notify(100, builder.build())
        } catch (e: SecurityException) {
            // TODO: Handle the exception
        }
    }
}

fun checkNotificationPermission(context: Context): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED)
}

fun checkScheduleExactAlarmPermission(alarmManager: AlarmManager): Boolean {
    return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) && (!alarmManager.canScheduleExactAlarms())
}
