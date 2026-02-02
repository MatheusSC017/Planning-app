package com.matheus.planningapp.ui.theme

sealed class DatabaseUiEvent {
    data class ShowError(val message: String): DatabaseUiEvent()
    object Saved: DatabaseUiEvent()
}