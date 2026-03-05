package com.matheus.planningapp.viewmodel.home

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matheus.planningapp.data.calendar.CalendarRepository
import com.matheus.planningapp.data.commitment.CommitmentEntity
import com.matheus.planningapp.data.commitment.CommitmentRepository
import com.matheus.planningapp.datastore.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import java.time.LocalDate
import java.time.YearMonth

class HomeViewModel(
    private val calendarRepository: CalendarRepository,
    private val commitmentRepository: CommitmentRepository,
    settingsRepository: SettingsRepository
): ViewModel() {
    init {
        viewModelScope.launch {
            calendarRepository.ensureDefaultCalendarExists()
        }
    }

    private val _selectedDate = MutableStateFlow(LocalDate.now())

    val uiState: StateFlow<HomeUiState> = combine(
        _selectedDate,
        calendarRepository.getCalendars(),
        settingsRepository.viewModeFlow,
        settingsRepository.notificationOptionFlow
    ) { selectedDate, calendars, viewModeFlow, notificationOption ->
        HomeUiState(
            selectedDate = selectedDate,
            calendars = calendars,
            viewMode = viewModeFlow,
            notificationOption = notificationOption
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState()
    )

    fun onSelectedDate(year: Int? = null, month: Int? = null, day: Int? = null) {
        _selectedDate.update {
            val newYear: Int = year?: it.year
            val newMonth: Int = month?: it.monthValue
            var newDay: Int = day?: it.dayOfMonth

            val newYearMonth = YearMonth.of(newYear, newMonth)
            newDay = newDay.coerceIn(1, newYearMonth.lengthOfMonth())

            LocalDate.of(newYear, newMonth, newDay)
        }
    }

    fun incrementYear() {
        onSelectedDate(year = _selectedDate.value.year + 1)
    }

    fun decrementYear() {
        onSelectedDate(year = _selectedDate.value.year - 1)
    }

    fun getCommitmentsForDay(dayStart: Instant, dayEnd: Instant, calendar: Long): Flow<List<CommitmentEntity>> {
        return commitmentRepository.getCommitmentsForDay(dayStart, dayEnd, calendar)
    }

    fun deleteCommitment(commitmentEntity: CommitmentEntity) {
        viewModelScope.launch {
            commitmentRepository.deleteCommitment(commitmentEntity)
        }
    }

}
