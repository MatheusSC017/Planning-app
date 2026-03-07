package com.matheus.planningapp.viewmodel.setting

enum class ViewOptions(val label: String) {
    COLUMN("Column"),
    GRID("Grid");

    companion object {
        fun fromName(name: String?): ViewOptions {
            return entries.firstOrNull { it.name == name } ?: entries.first()
        }
    }
}