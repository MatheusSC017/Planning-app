package com.matheus.planningapp.viewmodel.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matheus.planningapp.data.calendar.CalendarEntity
import com.matheus.planningapp.data.calendar.CalendarRepository
import com.matheus.planningapp.data.commitment.CommitmentEntity
import com.matheus.planningapp.data.commitment.CommitmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import java.time.LocalDate
import java.time.YearMonth

class HomeViewModel(
    private val calendarRepository: CalendarRepository,
    private val commitmentRepository: CommitmentRepository
): ViewModel() {
    init {
        viewModelScope.launch {
            calendarRepository.ensureDefaultCalendarExists()
        }
    }

    private val _uiState = MutableStateFlow(HomeState())
    val uiState: StateFlow<HomeState> = _uiState.asStateFlow()

    fun onSelectedDate(year: Int? = null, month: Int? = null, day: Int? = null) {
        _uiState.update {
            val newYear: Int = year?: it.selectedDate.year
            val newMonth: Int = month?: it.selectedDate.monthValue
            var newDay: Int = day?: it.selectedDate.dayOfMonth

            val newYearMonth = YearMonth.of(newYear, newMonth)
            newDay = newDay.coerceIn(1, newYearMonth.lengthOfMonth())

            it.copy(selectedDate = LocalDate.of(newYear, newMonth, newDay))
        }
    }

    fun incrementYear() {
        onSelectedDate(year = _uiState.value.selectedDate.year + 1)
    }

    fun decrementYear() {
        onSelectedDate(year = _uiState.value.selectedDate.year - 1)
    }

    val calendars: StateFlow<List<CalendarEntity>> = calendarRepository
        .getCalendars()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun getCommitmentsForDay(dayStart: Instant, dayEnd: Instant, calendar: Int): Flow<List<CommitmentEntity>> {
        return commitmentRepository.getCommitmentsForDay(dayStart, dayEnd, calendar)
    }

    fun deleteCommitment(commitmentEntity: CommitmentEntity) {
        viewModelScope.launch {
            commitmentRepository.deleteCommitment(commitmentEntity)
        }
    }

}