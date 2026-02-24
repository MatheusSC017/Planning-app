package com.matheus.planningapp.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.matheus.planningapp.viewmodel.setting.EmailOptions
import com.matheus.planningapp.viewmodel.setting.ViewOptions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

val VIEW_MODE = stringPreferencesKey("VIEW_MODE")
val EMAIL_OPTION = stringPreferencesKey("EMAIL_OPTION")

class SettingsRepository(private val context: Context) {

    val viewModeFlow: Flow<ViewOptions> = context.dataStore.data.map { preferences -> ViewOptions.fromName(preferences[VIEW_MODE]) }
    val emailOptionFlow: Flow<EmailOptions> = context.dataStore.data.map { preferences -> EmailOptions.fromName(preferences[EMAIL_OPTION]) }

    suspend fun saveSettings(viewOption: ViewOptions, emailOption: EmailOptions) {
        context.dataStore.edit { preferences ->
            preferences[VIEW_MODE] = viewOption.name
            preferences[EMAIL_OPTION] = emailOption.name
        }
    }

}
