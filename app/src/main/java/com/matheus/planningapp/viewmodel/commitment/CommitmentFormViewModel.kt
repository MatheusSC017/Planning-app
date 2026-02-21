package com.matheus.planningapp.viewmodel.commitment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matheus.planningapp.data.commitment.CommitmentEntity
import com.matheus.planningapp.data.commitment.CommitmentRepository
import com.matheus.planningapp.data.local.converters.Priority
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.minutes

class CommitmentFormViewModel(
    private val commitmentFormMode: CommitmentFormMode,
    private val commitmentRepository: CommitmentRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(CommitmentFormUiState())
    val uiState = _uiState.asStateFlow()

    fun onTitleChange(title: String) {
        _uiState.update {
            it.copy(title = title)
        }
    }

    fun onDescriptionChange(description: String) {
        _uiState.update {
            it.copy(description = description)
        }
    }

    fun onStartInstantChange(startInstant: Instant) {
        _uiState.update {
            it.copy(startInstant = startInstant)
        }
    }

    fun onEndInstantChange(endInstant: Instant) {
        _uiState.update {
            it.copy(endInstant = endInstant)
        }
    }

    fun onPriorityChange(priority: Priority) {
        _uiState.update {
            it.copy(priority = priority)
        }
    }

    private val _events = MutableSharedFlow<DatabaseUiEvent>()
    val events = _events.asSharedFlow()

    init {
        when (commitmentFormMode) {
            is CommitmentFormMode.Create -> {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        calendarId = commitmentFormMode.calendarId,
                        startInstant = commitmentFormMode.initialInstant,
                        endInstant = commitmentFormMode.initialInstant + 30.minutes,
                    )
                }
            }
            is CommitmentFormMode.Edit -> {
                viewModelScope.launch {
                    val commitmentEntity = commitmentRepository.getCommitment(commitmentFormMode.commitmentId)

                    if (commitmentEntity == null) {
                        _events.emit(DatabaseUiEvent.ShowError("Commitment not found"))
                        return@launch
                    }

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            id = commitmentEntity.id,
                            calendarId = commitmentEntity.calendar,
                            title = commitmentEntity.title,
                            description = commitmentEntity.description ?: "",
                            startInstant = commitmentEntity.startDateTime,
                            endInstant = commitmentEntity.endDateTime,
                            priority = commitmentEntity.priority,
                        )
                    }
                }
            }
        }
    }

    fun insertCommitment() {
        val commitmentEntity = CommitmentEntity(
            calendar = uiState.value.calendarId,
            title = uiState.value.title,
            description = uiState.value.description,
            startDateTime = uiState.value.startInstant,
            endDateTime =  uiState.value.endInstant,
            allDay = false,
            priority = uiState.value.priority
        )

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

    fun updateCommitment() {
        viewModelScope.launch {
            val oldCommitmentEntity = commitmentRepository.getCommitment(uiState.value.id!!)

            if (oldCommitmentEntity == null) {
                _events.emit(DatabaseUiEvent.ShowError("Commitment not found"))
                return@launch
            }

            val newCommitmentEntity = oldCommitmentEntity.copy(
                title = uiState.value.title,
                description = uiState.value.description,
                startDateTime = uiState.value.startInstant,
                endDateTime =  uiState.value.endInstant,
                priority = uiState.value.priority
            )

            // Check if start time is lesser than end time
            if (!verifyStartAndEndTime(newCommitmentEntity.startDateTime, newCommitmentEntity.endDateTime)) {
                _events.emit(
                    DatabaseUiEvent.ShowError("Start time must be lesser than End time")
                )
                return@launch
            }

            // Check if title is not empty
            if (newCommitmentEntity.title.isEmpty()) {
                _events.emit(
                    DatabaseUiEvent.ShowError("Title cannot be empty")
                )
                return@launch
            }

            // Check if there is a conflict with other commitments
            val conflictsNumbers: Int = commitmentRepository.checkSchedulingConflictsBetweenCommitments(
                newCommitmentEntity.startDateTime,
                newCommitmentEntity.endDateTime,
                newCommitmentEntity.calendar,
                newCommitmentEntity.id)

            if(conflictsNumbers > 0) {
                _events.emit(
                    DatabaseUiEvent.ShowError("There is a conflict with other commitments")
                )
                return@launch
            }

            commitmentRepository.updateCommitment(newCommitmentEntity)

            _events.emit(DatabaseUiEvent.Saved)
        }

    }

    private fun verifyStartAndEndTime(startDateTime: Instant, endDateTime: Instant): Boolean {
        return startDateTime.toEpochMilliseconds() < endDateTime.toEpochMilliseconds()
    }

}