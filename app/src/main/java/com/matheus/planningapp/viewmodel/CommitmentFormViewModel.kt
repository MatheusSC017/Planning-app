package com.matheus.planningapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matheus.planningapp.data.CommitmentEntity
import com.matheus.planningapp.data.CommitmentRepository
import com.matheus.planningapp.ui.theme.DatabaseUiEvent
import com.matheus.planningapp.viewmodel.CommitmentFormMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant

class CommitmentFormViewModel(
    private val commitmentFormMode: CommitmentFormMode,
    private val commitmentRepository: CommitmentRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(CommitmentFormState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<DatabaseUiEvent>()
    val events = _events.asSharedFlow()

    init {
        when (commitmentFormMode) {
            is CommitmentFormMode.Create -> {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        calendarId = commitmentFormMode.calendarId,
                        startInstant = commitmentFormMode.initialInstant
                    )
                }
            }
            is CommitmentFormMode.Edit -> {
                getCommitment(commitmentFormMode.commitmentId)
            }
        }
    }

    fun insertCommitment(commitmentEntity: CommitmentEntity) {
        viewModelScope.launch {
            // Check if start time is lesser than end time
            if (!verifyStartAndEndTime(commitmentEntity.startDateTime, commitmentEntity.endDateTime)) {
                _events.emit(
                    DatabaseUiEvent.ShowError("Start time must be lesser than End time")
                )
                return@launch
            }

            // Check if title is not empty
            if (commitmentEntity.title.isEmpty()) {
                _events.emit(
                    DatabaseUiEvent.ShowError("Title cannot be empty")
                )
                return@launch
            }

            // Check if there is a conflict with other commitments
            val conflictsNumbers: Int = commitmentRepository.checkSchedulingConflictsBetweenCommitments(
                commitmentEntity.startDateTime,
                commitmentEntity.endDateTime,
                commitmentEntity.calendar)

            if(conflictsNumbers > 0) {
                _events.emit(
                    DatabaseUiEvent.ShowError("There is a conflict with other commitments")
                )
                return@launch
            }

            commitmentRepository.insertCommitment(commitmentEntity)

            _events.emit(DatabaseUiEvent.Saved)
        }
    }

    private fun verifyStartAndEndTime(startDateTime: Instant, endDateTime: Instant): Boolean {
        return startDateTime.toEpochMilliseconds() < endDateTime.toEpochMilliseconds()
    }

    fun getCommitment(commitmentId: Int): CommitmentEntity {
        return commitmentRepository.getCommitment(commitmentId)
    }


}