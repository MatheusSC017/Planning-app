package com.matheus.planningapp.util.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.matheus.planningapp.R

object NotificationExtras {
    const val EXTRA_ID = 0
    const val EXTRA_TITLE = "Task notification"
    const val EXTRA_MESSAGE = "You have a task scheduled for now."
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