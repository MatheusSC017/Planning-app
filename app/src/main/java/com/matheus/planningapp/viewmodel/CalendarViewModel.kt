package com.matheus.planningapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matheus.planningapp.data.CalendarEntity
import com.matheus.planningapp.data.CalendarRepository
import com.matheus.planningapp.data.CommitmentEntity
import com.matheus.planningapp.data.CommitmentRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant

class CalendarViewModel(
    private val calendarRepository: CalendarRepository,
    private val commitmentRepository: CommitmentRepository
): ViewModel() {

    init {
        viewModelScope.launch {
            calendarRepository.ensureDefaultCalendarExists()
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
        if (!verifyStartAndEndTime(commitmentEntity.startDateTime, commitmentEntity.endDateTime)) {
            throw IllegalArgumentException("Start time must be lesser than End time")
        }

        /* TODO: Validate if Title is not Null */
        /* TODO: Validate multiple tasks in the same time slot */

        viewModelScope.launch { commitmentRepository.insertCommitment(commitmentEntity) }
    }

    private fun verifyStartAndEndTime(startDateTime: Instant, endDateTime: Instant): Boolean {
        return startDateTime.toEpochMilliseconds() < endDateTime.toEpochMilliseconds()
    }

}