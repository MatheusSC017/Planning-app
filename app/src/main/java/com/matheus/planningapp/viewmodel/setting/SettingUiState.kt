package com.matheus.planningapp.viewmodel.setting

data class SettingUiState (
    val viewMode: ViewOptions = ViewOptions.COLUMN,
    val emailOption: EmailOptions = EmailOptions.NO_SEND,
    val activeNotifications: Boolean = false
)
