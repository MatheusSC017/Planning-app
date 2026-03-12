package com.matheus.planningapp.util.enums

enum class ViewEnum(val label: String) {
    COLUMN("Column"),
    GRID("Grid");

    companion object {
        fun fromName(name: String?): ViewEnum {
            return entries.firstOrNull { it.name == name } ?: entries.first()
        }
    }
}