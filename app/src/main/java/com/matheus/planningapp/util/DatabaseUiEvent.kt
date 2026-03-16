package com.matheus.planningapp.util

sealed class DatabaseUiEvent {
    data class ShowError(val message: String): DatabaseUiEvent()
    object Saved: DatabaseUiEvent()
}