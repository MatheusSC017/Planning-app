package com.matheus.planningapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matheus.planningapp.data.CalendarEntity
import com.matheus.planningapp.data.CalendarRepository
import com.matheus.planningapp.data.CommitmentRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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

    fun getCommitments() {
        /* TODO */
    }

}