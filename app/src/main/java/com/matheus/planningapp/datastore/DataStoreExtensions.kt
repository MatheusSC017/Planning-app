package com.matheus.planningapp.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.matheus.planningapp.viewmodel.setting.NotificationEmailOptions
import com.matheus.planningapp.viewmodel.setting.ViewOptions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

val VIEW_MODE = stringPreferencesKey("VIEW_MODE")
val ACTIVE_EMAILS = booleanPreferencesKey("ACTIVE_EMAILS")
val EMAIL_OPTION = stringPreferencesKey("EMAIL_OPTION")
val ACTIVE_NOTIFICATIONS = booleanPreferencesKey("ACTIVE_NOTIFICATIONS")
val NOTIFICATION_OPTION = stringPreferencesKey("NOTIFICATION_OPTION")

class SettingsRepository(private val context: Context) {

    val viewModeFlow: Flow<ViewOptions> = context.dataStore.data.map { preferences -> ViewOptions.fromName(preferences[VIEW_MODE]) }
    val activeEmailFlow: Flow<Boolean> = context.dataStore.data.map { preferences -> preferences[ACTIVE_EMAILS] ?: false }
    val emailOptionFlow: Flow<NotificationEmailOptions> = context.dataStore.data.map { preferences -> NotificationEmailOptions.fromName(preferences[EMAIL_OPTION]) }
    val activeNotificationFlow: Flow<Boolean> = context.dataStore.data.map { preferences -> preferences[ACTIVE_NOTIFICATIONS] ?: false }
    val notificationOptionFlow: Flow<NotificationEmailOptions> = context.dataStore.data.map { preferences -> NotificationEmailOptions.fromName(preferences[NOTIFICATION_OPTION]) }

    suspend fun saveSettings(
        viewOption: ViewOptions,
        activeEmails: Boolean,
        emailOption: NotificationEmailOptions,
        activeNotifications: Boolean,
        notificationOption: NotificationEmailOptions
    ) {
        context.dataStore.edit { preferences ->
            preferences[VIEW_MODE] = viewOption.name
            preferences[ACTIVE_EMAILS] = activeEmails
            preferences[EMAIL_OPTION] = emailOption.name
            preferences[ACTIVE_NOTIFICATIONS] = activeNotifications
            preferences[NOTIFICATION_OPTION] = notificationOption.name
        }
    }

}
