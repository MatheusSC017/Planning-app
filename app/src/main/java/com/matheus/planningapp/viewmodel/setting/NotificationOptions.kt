package com.matheus.planningapp.viewmodel.setting

enum class NotificationOptions(val label: String) {
    NO_SEND("No send"),
    ALL_COMMITMENT("All commitments"),
    MEDIUM_AND_HIGH_PRIORITY("Medium and High priority"),
    ONLY_HIGH_PRIORITY("Only High priority");

    companion object {
        fun fromName(name: String?): NotificationOptions {
            return entries.firstOrNull { it.name == name } ?: entries.first()
        }
    }
}