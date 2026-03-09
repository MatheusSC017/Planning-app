package com.matheus.planningapp.viewmodel.setting

data class SettingUiState (
    val viewMode: ViewOptions = ViewOptions.COLUMN,
    val notificationOption: NotificationOptions = NotificationOptions.NO_SEND
)
