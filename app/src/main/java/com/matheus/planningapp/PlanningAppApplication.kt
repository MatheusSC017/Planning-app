package com.matheus.planningapp

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.matheus.planningapp.di.appModules
import com.matheus.planningapp.util.NotificationHelper
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class PlanningAppApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(applicationContext)
            modules(appModules)
        }

        val notificationHelper: NotificationHelper = NotificationHelper(applicationContext)
        notificationHelper.createNotificationChannel()
    }
}