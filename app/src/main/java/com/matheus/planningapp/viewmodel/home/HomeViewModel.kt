package com.matheus.planningapp.viewmodel.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matheus.planningapp.data.calendar.CalendarRepository
import com.matheus.planningapp.data.commitment.CommitmentEntity
import com.matheus.planningapp.data.commitment.CommitmentRepository
import com.matheus.planningapp.datastore.SettingsRepository
import com.matheus.planningapp.util.enums.DayOfWeekEnum
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.LocalDate
import java.time.YearMonth

class HomeViewModel(
    private val calendarRepository: CalendarRepository,
    private val commitmentRepository: CommitmentRepository,
    settingsRepository: SettingsRepository,
) : ViewModel() {
    init {
        viewModelScope.launch {
            calendarRepository.ensureDefaultCalendarExists()
        }
    }

    private val selectedDate = MutableStateFlow(LocalDate.now())

    val uiState: StateFlow<HomeUiState> =
        combine(
            selectedDate,
            calendarRepository.getCalendars(),
            settingsRepository.viewModeFlow,
            settingsRepository.notificationOptionFlow,
        ) { selectedDate, calendars, viewModeFlow, notificationOption ->
            HomeUiState(
                selectedDate = selectedDate,
                calendars = calendars,
                viewMode = viewModeFlow,
                notificationOption = notificationOption,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeUiState(),
        )

    fun onSelectedDate(
        year: Int? = null,
        month: Int? = null,
        day: Int? = null,
    ) {
        selectedDate.update {
            val newYear: Int = year ?: it.year
            val newMonth: Int = month ?: it.monthValue
            var newDay: Int = day ?: it.dayOfMonth

            val newYearMonth = YearMonth.of(newYear, newMonth)
            newDay = newDay.coerceIn(1, newYearMonth.lengthOfMonth())

            LocalDate.of(newYear, newMonth, newDay)
        }
    }

    fun incrementYear() {
        onSelectedDate(year = selectedDate.value.year + 1)
    }

    fun decrementYear() {
        onSelectedDate(year = selectedDate.value.year - 1)
    }

    fun getCommitmentsForDay(
        dayStart: Instant,
        dayEnd: Instant,
        calendar: Long,
    ): Flow<List<CommitmentEntity>> {
        val startDateTime: LocalDateTime = dayStart.toLocalDateTime(TimeZone.currentSystemDefault())
        return combine(
            commitmentRepository.getCommitmentsForDay(dayStart, dayEnd, calendar),
            commitmentRepository.getCommitmentByRecurrence(
                calendar = calendar,
                today = dayStart,
                dayOfWeek = DayOfWeekEnum.valueOf(startDateTime.dayOfWeek.name),
                dayOfMonth = startDateTime.dayOfMonth,
            ),
        ) { commitments, recurrenceCommitments ->
            commitments + recurrenceCommitments
        }
    }

    fun searchCommitments(
        query: String,
        calendar: Long,
    ): Flow<List<CommitmentEntity>> = commitmentRepository.searchCommitments(query, calendar)

    fun deleteCommitment(commitmentEntity: CommitmentEntity) {
        viewModelScope.launch {
            commitmentRepository.deleteCommitment(commitmentEntity)
        }
    }
}
