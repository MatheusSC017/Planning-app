package com.matheus.planningapp.util

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.matheus.planningapp.R
import com.matheus.planningapp.data.commitment.CommitmentEntity

const val CHANNEL_ID = "PlanningAppNotificationsId"
const val CHANNEL_NAME = "Planning App Notifications"


/* TODO: Put NotificationHelper in DI */
class NotificationHelper(private val context: Context) {
    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
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

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showNotification() {
        /* TODO: Method to check the creation of notifications, delete it */
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.outline_notifications_24)
            .setContentTitle(CHANNEL_NAME)
            .setContentText("Notification")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val manager = NotificationManagerCompat.from(context)

        manager.notify(1, builder.build())
    }

    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    fun scheduleTaskNotification(commitmentEntity: CommitmentEntity) {
        if (Build.VERSION.SDK_INT >= 31) {
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

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    commitmentEntity.startDateTime.toEpochMilliseconds(),
                    pendingIntent,
                )
            }
        }
    }
}

class NotificationReceiver: BroadcastReceiver() {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(context: Context, intent: Intent) {
        val title: String = intent.getStringExtra("title") ?: "Task notification"
        val message: String = intent.getStringExtra("message") ?: "You have a task scheduled for now."

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.outline_notifications_24)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        NotificationManagerCompat.from(context).notify(100, builder.build())
    }

}
