package com.matheus.planningapp.viewmodel.setting

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matheus.planningapp.data.commitment.CommitmentRepository
import com.matheus.planningapp.datastore.SettingsRepository
import com.matheus.planningapp.util.notification.TaskNotificationScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingViewModel(
    private val commitmentRepository: CommitmentRepository,
    private val settingsRepository: SettingsRepository,
    private val taskNotificationScheduler: TaskNotificationScheduler
): ViewModel() {

    val uiState: StateFlow<SettingUiState> = combine(
        settingsRepository.viewModeFlow,
        settingsRepository.activeEmailFlow,
        settingsRepository.emailOptionFlow,
        settingsRepository.activeNotificationFlow,
        settingsRepository.notificationOptionFlow
    ) { viewMode, activeEmails, emailOption, activeNotification, notificationOption ->
        SettingUiState(
            viewMode = viewMode,
            activeEmails = activeEmails,
            emailOption = emailOption,
            activeNotifications = activeNotification,
            notificationOption = notificationOption
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingUiState()
    )

    fun updateSettings(settingUiState: SettingUiState) {
        viewModelScope.launch {
            settingsRepository.saveSettings(settingUiState)
        }
    }

    fun setNotificationToFutureCommitment() {
        viewModelScope.launch {
            commitmentRepository.getFutureCommitments().forEach {
                taskNotificationScheduler.scheduleTaskNotification(it)
            }
        }
    }

    fun deleteNotificationToFutureCommitments() {
        viewModelScope.launch {
            commitmentRepository.getFutureCommitments().forEach {
                taskNotificationScheduler.cancelTaskNotification(it)
            }
        }
    }
}