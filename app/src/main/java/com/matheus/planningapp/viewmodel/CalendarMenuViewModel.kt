package com.matheus.planningapp.viewmodel

import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matheus.planningapp.data.CalendarEntity
import com.matheus.planningapp.data.CalendarRepository
import com.matheus.planningapp.ui.theme.DatabaseUiEvent
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
        /* TODO: Include verfication if calendar name is empty */
        viewModelScope.launch {
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
        /* TODO: Block remove is Default if calendar is the only Default, or change for other */
        viewModelScope.launch {
            if (calendarEntity.isDefault) {
                calendarRepository.setAllDefaultAsFalse()
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
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

}