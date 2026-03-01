package com.matheus.planningapp.viewmodel.setting

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matheus.planningapp.datastore.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingViewModel(
    application: Application
): ViewModel() {
    private val settingsRepository: SettingsRepository = SettingsRepository(application)

    val uiState: StateFlow<SettingUiState> = combine(
        settingsRepository.viewModeFlow,
        settingsRepository.emailOptionFlow,
        settingsRepository.activeNotificationFlow
    ) { viewMode, emailOption, activeNotification ->
        SettingUiState(
            viewMode = viewMode,
            emailOption = emailOption,
            activeNotifications = activeNotification
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingUiState()
    )

    fun updateSettings(viewMode: ViewOptions, emailOption: EmailOptions, activeNotifications: Boolean) {
        viewModelScope.launch {
            settingsRepository.saveSettings(viewMode, emailOption, activeNotifications)
        }
    }
}