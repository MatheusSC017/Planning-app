package com.matheus.planningapp.viewmodel.setting

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matheus.planningapp.datastore.SettingsRepository
import com.matheus.planningapp.ui.screens.EmailOptions
import com.matheus.planningapp.ui.screens.ViewOptions
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingViewModel(
    application: Application
): ViewModel() {
    /* TODO: Include State in the ViewModel */
    private val repository: SettingsRepository = SettingsRepository(application)

    val viewMode = repository.viewModeFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ViewOptions.COLUMN
    )

    val emailOption = repository.emailOptionFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        EmailOptions.NO_SEND
    )

    fun updateSettings(viewMode: ViewOptions, emailOption: EmailOptions) {
        viewModelScope.launch {
            repository.saveSettings(viewMode, emailOption)
        }
    }
}