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
    /* TODO: Include State in the ViewModel */
    private val repository: SettingsRepository = SettingsRepository(application)

    val uiState: StateFlow<SettingUiState> = combine(
        repository.viewModeFlow,
        repository.emailOptionFlow
    ) { viewMode, emailOption ->
        SettingUiState(
            viewMode = viewMode,
            emailOption = emailOption
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingUiState()
    )

    fun updateSettings(viewMode: ViewOptions, emailOption: EmailOptions) {
        viewModelScope.launch {
            repository.saveSettings(viewMode, emailOption)
        }
    }
}