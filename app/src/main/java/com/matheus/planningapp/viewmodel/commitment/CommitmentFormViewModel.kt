package com.matheus.planningapp.viewmodel.commitment

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matheus.planningapp.data.commitment.CommitmentEntity
import com.matheus.planningapp.data.commitment.CommitmentRepository
import com.matheus.planningapp.data.recurrence.RecurrenceEntity
import com.matheus.planningapp.data.recurrence.RecurrenceRepository
import com.matheus.planningapp.util.enums.PriorityEnum
import com.matheus.planningapp.datastore.SettingsRepository
import com.matheus.planningapp.util.DatabaseUiEvent
import com.matheus.planningapp.util.enums.DayOfWeekEnum
import com.matheus.planningapp.util.enums.FrequencyEnum
import com.matheus.planningapp.util.notification.TaskNotificationScheduler
import com.matheus.planningapp.util.enums.NotificationEnum
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.minutes

class CommitmentFormViewModel(
    commitmentFormMode: CommitmentFormMode,
    private val commitmentRepository: CommitmentRepository,
    settingsRepository: SettingsRepository,
    private val recurrenceRepository: RecurrenceRepository,
    private val taskNotificationScheduler: TaskNotificationScheduler
): ViewModel() {
    private val _events = MutableSharedFlow<DatabaseUiEvent>()
    val events = _events.asSharedFlow()

    private val _commitmentUiState: MutableStateFlow<CommitmentFormUiState> = MutableStateFlow(CommitmentFormUiState())
    val commitmentUiState: StateFlow<CommitmentFormUiState> = combine(
        _commitmentUiState,
        settingsRepository.notificationOptionFlow
    ) { currentUiState, notificationOption ->
        currentUiState.copy(
            notificationOption = notificationOption
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CommitmentFormUiState()
    )

    private val _recurrenceUiState: MutableStateFlow<RecurrenceFormUiState> = MutableStateFlow(RecurrenceFormUiState())
    val recurrenceUiState: StateFlow<RecurrenceFormUiState> = _recurrenceUiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = RecurrenceFormUiState()
    )

    fun onTitleChange(title: String) {
        _commitmentUiState.update {
            it.copy(title = title)
        }
    }

    fun onDescriptionChange(description: String) {
        _commitmentUiState.update {
            it.copy(description = description)
        }
    }

    fun onStartInstantChange(startInstant: Instant) {
        _commitmentUiState.update {
            it.copy(startInstant = startInstant)
        }
    }

    fun onEndInstantChange(endInstant: Instant) {
        _commitmentUiState.update {
            it.copy(endInstant = endInstant)
        }
    }

    fun onPriorityChange(priorityEnum: PriorityEnum) {
        _commitmentUiState.update {
            it.copy(priorityEnum = priorityEnum)
        }
    }

    fun onFrequencyChange(frequencyEnum: FrequencyEnum) {
        _recurrenceUiState.update {
            it.copy(frequencyEnum = frequencyEnum)
        }
    }

    fun onIntervalChange(interval: Int) {
        _recurrenceUiState.update {
            it.copy(interval = interval)
        }
    }

    fun onDaysOfWeekChange(daysOfWeekList: List<DayOfWeekEnum>) {
        _recurrenceUiState.update {
            it.copy(daysOfWeekList = daysOfWeekList)
        }
    }

    fun onDayOfMonthChange(dayOfMonth: Int) {
        _recurrenceUiState.update {
            it.copy(dayOfMonth = dayOfMonth)
        }
    }

    fun onRecurrenceFormActiveChange(isRecurrenceActive: Boolean) {
        _recurrenceUiState.update {
            it.copy(
                isRecurrenceActive = isRecurrenceActive
            )
        }
    }

    init {
        when (commitmentFormMode) {
            is CommitmentFormMode.Create -> {
                _commitmentUiState.update {
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

                    _commitmentUiState.update {
                        it.copy(
                            isLoading = false,
                            id = commitmentEntity.id,
                            calendarId = commitmentEntity.calendar,
                            title = commitmentEntity.title,
                            description = commitmentEntity.description ?: "",
                            startInstant = commitmentEntity.startDateTime,
                            endInstant = commitmentEntity.endDateTime,
                            priorityEnum = commitmentEntity.priorityEnum,
                        )
                    }

                    val recurrenceEntity = recurrenceRepository.getRecurrenceByCommitment(commitmentEntity.id)

                    if (recurrenceEntity != null) {
                        _recurrenceUiState.update {
                            it.copy(
                                id = recurrenceEntity.id,
                                isRecurrenceActive = true,
                                frequencyEnum = recurrenceEntity.frequency,
                                interval = recurrenceEntity.interval,
                                daysOfWeekList = recurrenceEntity.dayOfWeekList,
                                dayOfMonth = recurrenceEntity.dayOfMonth
                            )
                        }
                    }
                }
            }
        }
    }

    fun insertCommitment() {
        val commitmentEntity = CommitmentEntity(
            calendar = commitmentUiState.value.calendarId,
            title = commitmentUiState.value.title,
            description = commitmentUiState.value.description,
            startDateTime = commitmentUiState.value.startInstant,
            endDateTime =  commitmentUiState.value.endInstant,
            priorityEnum = commitmentUiState.value.priorityEnum
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

            val commitmentId = commitmentRepository.insertCommitment(commitmentEntity)

            if ((commitmentUiState.value.notificationOption != NotificationEnum.NO_SEND) &&
                (commitmentEntity.startDateTime > Clock.System.now())) {
                taskNotificationScheduler.scheduleTaskNotification(
                    commitmentEntity.copy(id = commitmentId)
                )
            }

            if (_recurrenceUiState.value.isRecurrenceActive) {
                insertRecurrence(commitmentId)
            }

            _events.emit(DatabaseUiEvent.Saved)
        }
    }

    fun updateCommitment() {
        viewModelScope.launch {
            val oldCommitmentEntity = commitmentRepository.getCommitment(commitmentUiState.value.id!!)

            if (oldCommitmentEntity == null) {
                _events.emit(DatabaseUiEvent.ShowError("Commitment not found"))
                return@launch
            }

            val newCommitmentEntity = oldCommitmentEntity.copy(
                title = commitmentUiState.value.title,
                description = commitmentUiState.value.description,
                startDateTime = commitmentUiState.value.startInstant,
                endDateTime =  commitmentUiState.value.endInstant,
                priorityEnum = commitmentUiState.value.priorityEnum
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

            if ((commitmentUiState.value.notificationOption != NotificationEnum.NO_SEND) &&
                (newCommitmentEntity.startDateTime > Clock.System.now())) {
                // TODO: To reduce the code complexity the task is always canceled and rescheduled when updated, check for a better solution
                taskNotificationScheduler.cancelTaskNotification(newCommitmentEntity)
                taskNotificationScheduler.scheduleTaskNotification(newCommitmentEntity)
            }

            val recurrenceEntity = recurrenceRepository.getRecurrenceByCommitment(commitmentUiState.value.id!!)
            if (_recurrenceUiState.value.isRecurrenceActive) {
                val newRecurrenceEntity = RecurrenceEntity(
                    id = recurrenceEntity?.id ?: 0,
                    commitment = _commitmentUiState.value.id!!,
                    frequency = _recurrenceUiState.value.frequencyEnum,
                    interval = _recurrenceUiState.value.interval,
                    dayOfWeekList = _recurrenceUiState.value.daysOfWeekList,
                    dayOfMonth = _recurrenceUiState.value.dayOfMonth
                )

                if (recurrenceEntity == null) recurrenceRepository.insert(newRecurrenceEntity) else recurrenceRepository.update(newRecurrenceEntity)
            } else {
                if (recurrenceEntity != null) recurrenceRepository.delete(recurrenceEntity)
            }
        }
    }

    private fun verifyStartAndEndTime(startDateTime: Instant, endDateTime: Instant): Boolean {
        return startDateTime.toEpochMilliseconds() < endDateTime.toEpochMilliseconds()
    }

    fun insertRecurrence(commitmentId: Long) {
        viewModelScope.launch {
            val recurrenceEntity = RecurrenceEntity(
                commitment = commitmentId,
                frequency = _recurrenceUiState.value.frequencyEnum,
                interval = _recurrenceUiState.value.interval,
                dayOfWeekList = _recurrenceUiState.value.daysOfWeekList,
                dayOfMonth = _recurrenceUiState.value.dayOfMonth
            )

            recurrenceRepository.insert(recurrenceEntity)
        }
    }

}