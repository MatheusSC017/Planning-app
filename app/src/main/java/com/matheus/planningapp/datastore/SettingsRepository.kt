package com.matheus.planningapp.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.matheus.planningapp.util.enums.NotificationEnum
import com.matheus.planningapp.viewmodel.setting.SettingUiState
import com.matheus.planningapp.util.enums.ViewEnum
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

val VIEW_MODE = stringPreferencesKey("VIEW_MODE")
val NOTIFICATION_OPTION = stringPreferencesKey("NOTIFICATION_OPTION")

class SettingsRepository(private val context: Context) {

    val viewModeFlow: Flow<ViewEnum> = context.dataStore.data.map { preferences -> ViewEnum.fromName(preferences[VIEW_MODE]) }
    val notificationOptionFlow: Flow<NotificationEnum> = context.dataStore.data.map { preferences -> NotificationEnum.fromName(preferences[NOTIFICATION_OPTION]) }

    suspend fun saveSettings(settingUiState: SettingUiState) {
        context.dataStore.edit { preferences ->
            preferences[VIEW_MODE] = settingUiState.viewMode.name
            preferences[NOTIFICATION_OPTION] = settingUiState.notificationOption.name
        }
    }

}
