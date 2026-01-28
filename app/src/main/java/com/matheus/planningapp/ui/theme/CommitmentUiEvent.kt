package com.matheus.planningapp.ui.theme

sealed class CommitmentUiEvent {
    data class ShowError(val message: String): CommitmentUiEvent()
    object Saved: CommitmentUiEvent()
}