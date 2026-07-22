package com.matheus.planningapp.util

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import com.matheus.planningapp.util.notification.canScheduleExact
import com.matheus.planningapp.util.notification.hasNotificationPermission

class PermissionManager(
    private val context: Context,
) {
    fun requestNotificationAndAlarmPermissions(
        notificationPermissionLauncher: ActivityResultLauncher<String>,
        scheduleExactAlarmLauncher: ActivityResultLauncher<Intent>,
    ): Boolean {
        if (!context.hasNotificationPermission()) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            return false
        }

        val alarmManager = context.getSystemService(AlarmManager::class.java)
        if (!alarmManager.canScheduleExact()) {
            scheduleExactAlarmLauncher.launch(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
            return false
        }

        return true
    }

    fun hasNotificationPermission(): Boolean = context.hasNotificationPermission()

    fun canScheduleExactAlarm(): Boolean {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        return alarmManager.canScheduleExact()
    }

    fun isNotificationPolicyAccessGranted(): Boolean {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return notificationManager.isNotificationPolicyAccessGranted
    }

    fun requestNotificationPolicyAccess(launcher: ActivityResultLauncher<Intent>) {
        val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
        launcher.launch(intent)
    }
}
