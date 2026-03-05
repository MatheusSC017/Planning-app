package com.matheus.planningapp.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.matheus.planningapp.viewmodel.setting.NotificationEmailOptions
import com.matheus.planningapp.viewmodel.setting.SettingUiState
import com.matheus.planningapp.viewmodel.setting.ViewOptions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

val VIEW_MODE = stringPreferencesKey("VIEW_MODE")
val EMAIL_OPTION = stringPreferencesKey("EMAIL_OPTION")
val NOTIFICATION_OPTION = stringPreferencesKey("NOTIFICATION_OPTION")

class SettingsRepository(private val context: Context) {

    val viewModeFlow: Flow<ViewOptions> = context.dataStore.data.map { preferences -> ViewOptions.fromName(preferences[VIEW_MODE]) }
    val emailOptionFlow: Flow<NotificationEmailOptions> = context.dataStore.data.map { preferences -> NotificationEmailOptions.fromName(preferences[EMAIL_OPTION]) }
    val notificationOptionFlow: Flow<NotificationEmailOptions> = context.dataStore.data.map { preferences -> NotificationEmailOptions.fromName(preferences[NOTIFICATION_OPTION]) }

    suspend fun saveSettings(settingUiState: SettingUiState) {
        context.dataStore.edit { preferences ->
            preferences[VIEW_MODE] = settingUiState.viewMode.name
            preferences[EMAIL_OPTION] = settingUiState.emailOption.name
            preferences[NOTIFICATION_OPTION] = settingUiState.notificationOption.name
        }
    }

}
