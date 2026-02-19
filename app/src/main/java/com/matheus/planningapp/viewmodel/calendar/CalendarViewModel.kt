package com.matheus.planningapp.viewmodel.calendar

import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matheus.planningapp.data.calendar.CalendarEntity
import com.matheus.planningapp.data.calendar.CalendarRepository
import com.matheus.planningapp.viewmodel.commitment.DatabaseUiEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CalendarMenuViewModel(
    private val calendarRepository: CalendarRepository
): ViewModel() {
    private val _events = MutableSharedFlow<DatabaseUiEvent>()
    val events = _events.asSharedFlow()

    fun insertCalendar(calendarEntity: CalendarEntity) {
        viewModelScope.launch {
            // Check if calendar name is empty
            if (calendarEntity.name.isEmpty() || calendarEntity.name.isBlank()) {
                _events.emit(
                    DatabaseUiEvent.ShowError("Calendar name cannot be empty")
                )
                return@launch
            }

            try {
                if (calendarEntity.isDefault) {
                    calendarRepository.setAllDefaultAsFalse()
                }

                calendarRepository.insertCalendar(calendarEntity)

                _events.emit(DatabaseUiEvent.Saved)
            } catch (e: SQLiteConstraintException) {
                _events.emit(
                    DatabaseUiEvent.ShowError("Calendar name must be unique")
                )
                return@launch
            }
        }
    }

    fun updateCalendar(calendarEntity: CalendarEntity) {
        viewModelScope.launch {
            // Check if calendar name is empty
            if (calendarEntity.name.isEmpty() || calendarEntity.name.isBlank()) {
                _events.emit(
                    DatabaseUiEvent.ShowError("Calendar name cannot be empty")
                )
                return@launch
            }

            // If the calendar is the default, set all others to false.
            if (calendarEntity.isDefault) {
                calendarRepository.setAllDefaultAsFalse()
            }

            val currentCalendarEntity = calendarRepository.getCalendarById(calendarEntity.id)
            // Check if the current calendar is the default; if it is, a different calendar must be set first.
            if (currentCalendarEntity?.isDefault == true && !calendarEntity.isDefault) {
                _events.emit(
                    DatabaseUiEvent.ShowError("The default calendar cannot be changed.")
                )
                return@launch
            }

            calendarRepository.updateCalendar(calendarEntity)
        }
    }

    fun deleteCalendar(calendarEntity: CalendarEntity) {
        viewModelScope.launch {
            if (calendarEntity.isDefault) {
                _events.emit(
                    DatabaseUiEvent.ShowError("The default calendar cannot be deleted.")
                )
                return@launch
            }

            calendarRepository.deleteCalendar(calendarEntity)

            _events.emit(DatabaseUiEvent.Saved)
        }
    }

    val calendars: StateFlow<List<CalendarEntity>> = calendarRepository
        .getCalendars()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

}