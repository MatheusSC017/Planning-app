package com.matheus.planningapp.util.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object NotificationConfig {
    const val CHANNEL_ID = "PlanningAppNotificationsId"
    const val CHANNEL_NAME = "Planning App Notifications"
    const val NUDGE_CHANNEL_ID = "PlanningAppFocusNudgeId"
    const val NUDGE_CHANNEL_NAME = "Focus Mode Nudges"
}

class NotificationChannelManager(
    private val context: Context,
) {
    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(NotificationManager::class.java)
            
            // General channel
            val channel = NotificationChannel(
                NotificationConfig.CHANNEL_ID,
                NotificationConfig.CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply {
                description = "Channel for notifications of scheduled tasks."
            }
            manager.createNotificationChannel(channel)

            // Dedicated High Priority channel for Nudges
            val nudgeChannel = NotificationChannel(
                NotificationConfig.NUDGE_CHANNEL_ID,
                NotificationConfig.NUDGE_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = "Urgent nudges to stay focused during focus sessions."
                enableVibration(true)
                setShowBadge(true)
            }
            manager.createNotificationChannel(nudgeChannel)
        }
    }
}
