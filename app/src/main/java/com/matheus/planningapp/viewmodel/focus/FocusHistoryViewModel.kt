package com.matheus.planningapp.viewmodel.focus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matheus.planningapp.data.focus.FocusSessionEntity
import com.matheus.planningapp.data.focus.FocusSessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FocusHistoryViewModel(private val repository: FocusSessionRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(FocusHistoryUiState())
    val uiState: StateFlow<FocusHistoryUiState> = _uiState.asStateFlow()

    private val pageSize = 20

    init {
        loadNextPage()
    }

    fun loadNextPage() {
        if (_uiState.value.isLoading || _uiState.value.isLastPage) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val currentPage = _uiState.value.currentPage
                val newSessions = repository.getSessionsPaged(currentPage, pageSize)
                val totalCount = repository.getTotalSessionsCount()
                
                _uiState.update { state ->
                    val updatedSessions = state.sessions + newSessions
                    state.copy(
                        sessions = updatedSessions,
                        currentPage = currentPage + 1,
                        isLoading = false,
                        isLastPage = updatedSessions.size >= totalCount || newSessions.isEmpty()
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}

data class FocusHistoryUiState(
    val sessions: List<FocusSessionEntity> = emptyList(),
    val isLoading: Boolean = false,
    val currentPage: Int = 0,
    val isLastPage: Boolean = false
)
