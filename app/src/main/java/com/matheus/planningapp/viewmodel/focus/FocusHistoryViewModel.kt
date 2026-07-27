package com.matheus.planningapp.viewmodel.focus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matheus.planningapp.data.commitment.CommitmentRepository
import com.matheus.planningapp.data.focus.FocusSessionEntity
import com.matheus.planningapp.data.focus.FocusSessionRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FocusHistoryViewModel(
    private val repository: FocusSessionRepository,
    private val commitmentRepository: CommitmentRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(FocusHistoryUiState())
    val uiState: StateFlow<FocusHistoryUiState> = _uiState.asStateFlow()

    private val pageSize = 20

    init {
        loadPage(0)
    }

    fun loadPage(page: Int) {
        if (page < 0) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val sessions = repository.getSessionsPaged(page, pageSize)
                val totalCount = repository.getTotalSessionsCount()
                val totalPages = if (totalCount == 0) 1 else (totalCount + pageSize - 1) / pageSize

                val sessionUiModels = sessions.map { session ->
                    async {
                        val commitmentTitle = session.commitmentId?.let { 
                            commitmentRepository.getCommitment(it)?.title 
                        }
                        FocusSessionUiModel(session, commitmentTitle)
                    }
                }.awaitAll()

                _uiState.update { state ->
                    state.copy(
                        sessions = sessionUiModels,
                        currentPage = page,
                        totalPages = totalPages,
                        isLoading = false,
                        isLastPage = (page + 1) >= totalPages
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onNextPage() {
        if (!_uiState.value.isLastPage) {
            loadPage(_uiState.value.currentPage + 1)
        }
    }

    fun onPreviousPage() {
        if (_uiState.value.currentPage > 0) {
            loadPage(_uiState.value.currentPage - 1)
        }
    }
}

data class FocusSessionUiModel(
    val session: FocusSessionEntity,
    val commitmentTitle: String? = null
)

data class FocusHistoryUiState(
    val sessions: List<FocusSessionUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val currentPage: Int = 0,
    val totalPages: Int = 1,
    val isLastPage: Boolean = false
)
