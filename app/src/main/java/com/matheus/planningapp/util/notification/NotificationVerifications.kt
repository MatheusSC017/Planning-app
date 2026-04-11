package com.matheus.planningapp.util.notification

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

fun Context.hasNotificationPermission(): Boolean =
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
        (
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED
        )

fun AlarmManager.canScheduleExact(): Boolean = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) && (this.canScheduleExactAlarms())
