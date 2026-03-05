package com.matheus.planningapp.viewmodel.setting

enum class NotificationEmailOptions(val label: String) {
    ALL_COMMITMENT("All commitments"),
    MEDIUM_AND_HIGH_PRIORITY("Medium and High priority"),
    ONLY_HIGH_PRIORITY("Only High priority");

    companion object {
        fun fromLabel(label: String?): NotificationEmailOptions {
            return entries.firstOrNull({ it.label == label} ) ?: entries.first()
        }

        fun fromName(name: String?): NotificationEmailOptions {
            return entries.firstOrNull { it.name == name } ?: entries.first()
        }
    }
}