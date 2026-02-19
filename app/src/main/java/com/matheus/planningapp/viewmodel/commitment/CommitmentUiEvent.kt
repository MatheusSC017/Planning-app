package com.matheus.planningapp.viewmodel.commitment

sealed class DatabaseUiEvent {
    data class ShowError(val message: String): DatabaseUiEvent()
    object Saved: DatabaseUiEvent()
}