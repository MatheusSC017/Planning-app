package com.matheus.planningapp.ui.screens

enum class ViewOptions(val label: String) {
    COLUMN("Column"),
    GRID("Grid");

    companion object {
        fun fromLabel(label: String?): ViewOptions {
            return entries.firstOrNull { it.label == label } ?: entries.first()
        }

        fun fromName(name: String?): ViewOptions {
            return entries.firstOrNull { it.name == name } ?: entries.first()
        }
    }
}