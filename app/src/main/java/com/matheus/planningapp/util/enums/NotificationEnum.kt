package com.matheus.planningapp.util.enums

enum class NotificationEnum(val label: String) {
    NO_SEND("No send"),
    ALL_COMMITMENT("All commitments"),
    MEDIUM_AND_HIGH_PRIORITY("Medium and High priority"),
    ONLY_HIGH_PRIORITY("Only High priority");

    companion object {
        fun fromName(name: String?): NotificationEnum {
            return entries.firstOrNull { it.name == name } ?: entries.first()
        }
    }
}