package com.matheus.planningapp.viewmodel.setting

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matheus.planningapp.data.commitment.CommitmentRepository
import com.matheus.planningapp.datastore.SettingsRepository
import com.matheus.planningapp.util.notification.TaskNotificationScheduler
import com.matheus.planningapp.util.notification.canScheduleExact
import com.matheus.planningapp.util.notification.hasNotificationPermission
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingViewModel(
    private val context: Context,
    private val commitmentRepository: CommitmentRepository,
    private val settingsRepository: SettingsRepository,
    private val taskNotificationScheduler: TaskNotificationScheduler
): ViewModel() {

    val uiState: StateFlow<SettingUiState> = combine(
        settingsRepository.viewModeFlow,
        settingsRepository.emailOptionFlow,
        settingsRepository.notificationOptionFlow
    ) { viewMode, emailOption, notificationOption ->
        SettingUiState(
            viewMode = viewMode,
            emailOption = emailOption,
            notificationOption = notificationOption
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingUiState()
    )

    fun updateSettings(settingUiState: SettingUiState, notificationPermissionLauncher: ActivityResultLauncher<String>) {
        viewModelScope.launch {
            val currentNotificationOption: NotificationEmailOptions = uiState.value.notificationOption

            settingsRepository.saveSettings(settingUiState)

            if (currentNotificationOption != settingUiState.notificationOption) {
                if (settingUiState.notificationOption == NotificationEmailOptions.NO_SEND) {
                    deleteNotificationToFutureCommitments()
                } else {
                    if (currentNotificationOption != NotificationEmailOptions.NO_SEND) deleteNotificationToFutureCommitments()

                    requestNotificationPermission(notificationPermissionLauncher)
                }
            }
        }
    }

    private fun requestNotificationPermission(
        notificationPermissionLauncher: ActivityResultLauncher<String>
    ) {
        if (!context.hasNotificationPermission()) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            return
        }

        val alarmManager = context.getSystemService(AlarmManager::class.java)
        if (!alarmManager.canScheduleExact()) {
            context.startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
            return
        }

        setNotificationToFutureCommitment()
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