package com.matheus.planningapp.util.notification

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

fun Context.hasNotificationPermission(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    } else {
        NotificationManagerCompat.from(this).areNotificationsEnabled()
    }
}

fun AlarmManager.canScheduleExact(): Boolean = 
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        this.canScheduleExactAlarms()
    } else {
        true
    }
