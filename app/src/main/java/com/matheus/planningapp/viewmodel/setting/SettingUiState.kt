package com.matheus.planningapp.viewmodel.setting

import com.matheus.planningapp.util.enums.NotificationEnum
import com.matheus.planningapp.util.enums.ViewEnum

data class SettingUiState(
    val viewMode: ViewEnum = ViewEnum.COLUMN,
    val notificationOption: NotificationEnum = NotificationEnum.NO_SEND,
)
