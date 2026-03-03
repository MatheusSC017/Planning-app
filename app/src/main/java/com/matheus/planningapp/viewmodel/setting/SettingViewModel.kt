package com.matheus.planningapp.viewmodel.setting

import android.app.Application
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matheus.planningapp.data.commitment.CommitmentEntity
import com.matheus.planningapp.data.commitment.CommitmentRepository
import com.matheus.planningapp.datastore.SettingsRepository
import com.matheus.planningapp.util.NotificationHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingViewModel(
    application: Application,
    private val commitmentRepository: CommitmentRepository
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

    fun setNotificationToFutureCommitment(context: Context) {
        val notificationHelper = NotificationHelper(context)

        viewModelScope.launch {
            commitmentRepository.getFutureCommitments().forEach {
                notificationHelper.scheduleTaskNotification(it)
            }
        }
    }

    fun deleteNotificationToFutureCommitments(context: Context) {
        val notificationHelper = NotificationHelper(context)

        viewModelScope.launch {
            commitmentRepository.getFutureCommitments().forEach {
                notificationHelper.cancelTaskNotification(it)
            }
        }
    }
}