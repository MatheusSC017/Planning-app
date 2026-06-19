package com.matheus.planningapp.viewmodel.recurrence

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matheus.planningapp.data.calendar.CalendarEntity
import com.matheus.planningapp.data.calendar.CalendarRepository
import com.matheus.planningapp.data.recurrence.CommitmentRecurrenceDataClass
import com.matheus.planningapp.data.recurrence.RecurrenceEntity
import com.matheus.planningapp.data.recurrence.RecurrenceRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RecurrenceViewModel(
    calendarRepository: CalendarRepository,
    private val recurrenceRepository: RecurrenceRepository,
) : ViewModel() {
    private val _selectedCalendar = MutableStateFlow<CalendarEntity?>(null)
    val selectedCalendar: StateFlow<CalendarEntity?> = _selectedCalendar

    fun onCalendarSelected(calendar: CalendarEntity) {
        _selectedCalendar.value = calendar
    }

    fun initializeDefaultCalendar(calendars: List<CalendarEntity>) {
        if (_selectedCalendar.value == null && calendars.isNotEmpty()) {
            _selectedCalendar.value = calendars.first()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val filteredRecurrences: StateFlow<List<CommitmentRecurrenceDataClass>> =
        _selectedCalendar
            .flatMapLatest { calendar ->
                if (calendar == null) flowOf(emptyList())
                else recurrenceRepository.getRecurrenceByCalendar(calendar.id)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    val uiState: StateFlow<RecurrenceUiState> =
        combine(
            calendarRepository.getCalendars(),
        ) { values ->
            RecurrenceUiState(
                calendars = values[0],
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = RecurrenceUiState(),
        )

    fun deleteRecurrence(recurrenceId: Long) {
        viewModelScope.launch {
            val recurrence: RecurrenceEntity? = recurrenceRepository.getRecurrenceById(recurrenceId = recurrenceId)

            if (recurrence != null) {
                recurrenceRepository.delete(recurrence)
            }
        }
    }
}
