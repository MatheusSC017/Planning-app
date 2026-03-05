package com.matheus.planningapp.viewmodel.setting

data class SettingUiState (
    val viewMode: ViewOptions = ViewOptions.COLUMN,
    val emailOption: NotificationEmailOptions = NotificationEmailOptions.NO_SEND,
    val notificationOption: NotificationEmailOptions = NotificationEmailOptions.NO_SEND
)
