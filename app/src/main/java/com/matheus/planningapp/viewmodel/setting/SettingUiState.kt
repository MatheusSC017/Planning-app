package com.matheus.planningapp.viewmodel.setting

data class SettingUiState (
    val viewMode: ViewOptions = ViewOptions.COLUMN,
    val activeEmails: Boolean = false,
    val emailOption: NotificationEmailOptions = NotificationEmailOptions.NO_SEND,
    val activeNotifications: Boolean = false,
    val notificationOption: NotificationEmailOptions = NotificationEmailOptions.NO_SEND
)
