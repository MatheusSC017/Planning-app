package com.matheus.planningapp.util

import android.Manifest
import android.app.AlarmManager
import android.app.AppOpsManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Process
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.AppOpsManagerCompat
import com.matheus.planningapp.util.notification.canScheduleExact
import com.matheus.planningapp.util.notification.hasNotificationPermission

class PermissionManager(
    private val context: Context,
) {
    fun requestNotificationAndAlarmPermissions(
        notificationPermissionLauncher: ActivityResultLauncher<String>,
        scheduleExactAlarmLauncher: ActivityResultLauncher<Intent>,
    ): Boolean {
        if (!hasNotificationPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
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

    fun requestNotificationPermission(launcher: ActivityResultLauncher<String>) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

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

    fun hasUsageStatsPermission(): Boolean {
        val mode = AppOpsManagerCompat.noteOpNoThrow(
            context,
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName
        )
        return mode == AppOpsManagerCompat.MODE_ALLOWED
    }

    fun requestUsageStatsPermission(launcher: ActivityResultLauncher<Intent>) {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        launcher.launch(intent)
    }
}
