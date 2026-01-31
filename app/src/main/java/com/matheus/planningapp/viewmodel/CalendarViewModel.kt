package com.matheus.planningapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matheus.planningapp.data.CalendarEntity
import com.matheus.planningapp.data.CalendarRepository
import com.matheus.planningapp.data.CommitmentEntity
import com.matheus.planningapp.data.CommitmentRepository
import com.matheus.planningapp.ui.theme.CommitmentUiEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant

class CalendarViewModel(
    private val calendarRepository: CalendarRepository,
    private val commitmentRepository: CommitmentRepository
): ViewModel() {
    private val _events = MutableSharedFlow<CommitmentUiEvent>()
    val events = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            calendarRepository.ensureDefaultCalendarExists()
        }
    }

    fun insertCalendar(calendarEntity: CalendarEntity) {
        viewModelScope.launch {
            if (calendarEntity.isDefault) {
                calendarRepository.setAllDefaultAsFalse()
            }

            calendarRepository.insertCalendar(calendarEntity)
        }
    }

    val calendars: StateFlow<List<CalendarEntity>> =
        calendarRepository.getCalendars()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    fun insertCommitment(commitmentEntity: CommitmentEntity) {
        viewModelScope.launch {
            // Check if start time is lesser than end time
            if (!verifyStartAndEndTime(commitmentEntity.startDateTime, commitmentEntity.endDateTime)) {
                _events.emit(
                    CommitmentUiEvent.ShowError("Start time must be lesser than End time")
                )
                return@launch
            }

            // Check if title is not empty
            if (commitmentEntity.title.isEmpty()) {
                _events.emit(
                    CommitmentUiEvent.ShowError("Title cannot be empty")
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
                    CommitmentUiEvent.ShowError("There is a conflict with other commitments")
                )
                return@launch
            }

            commitmentRepository.insertCommitment(commitmentEntity)

            _events.emit(CommitmentUiEvent.Saved)
        }
    }

    private fun verifyStartAndEndTime(startDateTime: Instant, endDateTime: Instant): Boolean {
        return startDateTime.toEpochMilliseconds() < endDateTime.toEpochMilliseconds()
    }

    fun getCommitmentsForDay(dayStart: Instant, dayEnd: Instant, calendar: Int): Flow<List<CommitmentEntity>> {
        return commitmentRepository.getCommitmentsForDay(dayStart, dayEnd, calendar)
    }

}