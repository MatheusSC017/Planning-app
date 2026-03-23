package com.matheus.planningapp.viewmodel.recurrence

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matheus.planningapp.data.calendar.CalendarRepository
import com.matheus.planningapp.data.recurrence.CommitmentRecurrenceDataClass
import com.matheus.planningapp.data.recurrence.RecurrenceEntity
import com.matheus.planningapp.data.recurrence.RecurrenceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class RecurrenceViewModel(
    calendarRepository: CalendarRepository,
    private val recurrenceRepository: RecurrenceRepository
): ViewModel() {

    val uiState: StateFlow<RecurrenceUiState> = combine(
        calendarRepository.getCalendars()
    ) { values ->
        RecurrenceUiState(
            calendars = values[0]
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = RecurrenceUiState()
    )

    fun getRecurrencesByCalendar(calendarId: Long): Flow<List<CommitmentRecurrenceDataClass>> {
        return recurrenceRepository.getRecurrenceByCalendar(calendarId = calendarId)
    }

}