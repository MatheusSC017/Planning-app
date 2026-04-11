package com.matheus.planningapp

import android.app.Application
import com.matheus.planningapp.di.appModules
import com.matheus.planningapp.util.notification.NotificationChannelManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class PlanningAppApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(applicationContext)
            modules(appModules)
        }

        val notificationChannelManager = NotificationChannelManager(applicationContext)
        notificationChannelManager.createNotificationChannel()
    }
}
