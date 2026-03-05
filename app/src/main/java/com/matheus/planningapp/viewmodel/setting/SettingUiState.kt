package com.matheus.planningapp.viewmodel.setting

data class SettingUiState (
    val viewMode: ViewOptions = ViewOptions.COLUMN,
    val activeEmails: Boolean = false,
    val emailOption: NotificationEmailOptions = NotificationEmailOptions.ALL_COMMITMENT,
    val activeNotifications: Boolean = false,
    val notificationOption: NotificationEmailOptions = NotificationEmailOptions.ALL_COMMITMENT
)
