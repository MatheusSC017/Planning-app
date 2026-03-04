package com.matheus.planningapp.viewmodel.setting

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matheus.planningapp.data.commitment.CommitmentRepository
import com.matheus.planningapp.datastore.SettingsRepository
import com.matheus.planningapp.util.TaskNotificationScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingViewModel(
    private val commitmentRepository: CommitmentRepository,
    private val settingsRepository: SettingsRepository
): ViewModel() {

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
        val taskNotificationScheduler = TaskNotificationScheduler(context)

        viewModelScope.launch {
            commitmentRepository.getFutureCommitments().forEach {
                taskNotificationScheduler.scheduleTaskNotification(it)
            }
        }
    }

    fun deleteNotificationToFutureCommitments(context: Context) {
        val taskNotificationScheduler = TaskNotificationScheduler(context)

        viewModelScope.launch {
            commitmentRepository.getFutureCommitments().forEach {
                taskNotificationScheduler.cancelTaskNotification(it)
            }
        }
    }
}