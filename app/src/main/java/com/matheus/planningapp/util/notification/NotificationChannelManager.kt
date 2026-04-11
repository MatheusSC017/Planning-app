package com.matheus.planningapp.util.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object NotificationConfig {
    const val CHANNEL_ID = "PlanningAppNotificationsId"
    const val CHANNEL_NAME = "Planning App Notifications"
}

class NotificationChannelManager(
    private val context: Context,
) {
    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel: NotificationChannel =
                NotificationChannel(
                    NotificationConfig.CHANNEL_ID,
                    NotificationConfig.CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT,
                ).apply {
                    description = "Channel for notifications of scheduled tasks."
                }

            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}
