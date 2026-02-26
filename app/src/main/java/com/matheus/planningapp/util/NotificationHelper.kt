package com.matheus.planningapp.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.matheus.planningapp.R


class NotificationHelper(private val context: Context) {
    private val CHANNEL_ID = "PlanningAppNotificationsId"
    private val CHANNEL_NAME = "Planning App Notifications"

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
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.outline_notifications_24)
            .setContentTitle(CHANNEL_NAME)
            .setContentTitle("Notification")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val manager = NotificationManagerCompat.from(context)

        manager.notify(1, builder.build())
    }
}