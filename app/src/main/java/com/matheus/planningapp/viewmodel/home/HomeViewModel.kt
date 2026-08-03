package com.matheus.planningapp.viewmodel.home

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matheus.planningapp.data.calendar.CalendarRepository
import com.matheus.planningapp.data.commitment.CommitmentEntity
import com.matheus.planningapp.data.commitment.CommitmentRepository
import com.matheus.planningapp.data.reminder.ReminderEntity
import com.matheus.planningapp.data.reminder.ReminderRepository
import com.matheus.planningapp.datastore.SettingsRepository
import com.matheus.planningapp.ui.theme.strings.StringsRepository
import com.matheus.planningapp.util.DatabaseUiEvent
import com.matheus.planningapp.util.PermissionManager
import com.matheus.planningapp.util.enums.DayOfWeekEnum
import com.matheus.planningapp.util.notification.TaskNotificationScheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.LocalDate
import java.time.YearMonth
import kotlin.time.Duration.Companion.minutes

class HomeViewModel(
    private val calendarRepository: CalendarRepository,
    private val commitmentRepository: CommitmentRepository,
    private val reminderRepository: ReminderRepository,
    private val permissionManager: PermissionManager,
    settingsRepository: SettingsRepository,
    private val taskNotificationScheduler: TaskNotificationScheduler,
    private val strings: StringsRepository,
) : ViewModel() {
    private val _events = MutableSharedFlow<DatabaseUiEvent>()
    val events = _events.asSharedFlow()

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

    fun moveCommitment(
        commitment: CommitmentEntity,
        newStartTime: Instant,
    ) {
        val duration = commitment.endDateTime - commitment.startDateTime
        val newEndTime = newStartTime + duration

        viewModelScope.launch {
            val conflicts =
                commitmentRepository.checkSchedulingConflictsBetweenCommitments(
                    startDateTime = newStartTime,
                    endDateTime = newEndTime,
                    calendarId = commitment.calendar,
                    commitmentId = commitment.id,
                )

            if (conflicts > 0) {
                _events.emit(DatabaseUiEvent.ShowError(strings.commitmentConflictError))
                return@launch
            }

            val updatedCommitment =
                commitment.copy(
                    startDateTime = newStartTime,
                    endDateTime = newEndTime,
                    updatedAt = Clock.System.now(),
                )
            commitmentRepository.updateCommitment(updatedCommitment)
        }
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

    fun getRemindersByCommitment(commitmentId: Long): Flow<List<ReminderEntity>> = reminderRepository.getRemindersByCommitment(commitmentId)

    fun insertReminder(
        commitmentEntity: CommitmentEntity,
        minutesBeforeCommitment: Int,
        notificationPermissionLauncher: ActivityResultLauncher<String>,
        scheduleExactAlarmLauncher: ActivityResultLauncher<Intent>,
    ) {
        if (!permissionManager.requestNotificationAndAlarmPermissions(notificationPermissionLauncher, scheduleExactAlarmLauncher)) {
            return
        }

        if (isPastCommitment(commitmentEntity.startDateTime, minutesBeforeCommitment)) {
            viewModelScope.launch {
                _events.emit(
                    DatabaseUiEvent.ShowError(strings.pastReminderError),
                )
            }
            return
        }

        val reminderEntity =
            ReminderEntity(
                commitment = commitmentEntity.id,
                minutesBeforeCommitment = minutesBeforeCommitment,
            )

        viewModelScope.launch {
            val remiderId: Long = reminderRepository.insert(reminderEntity)

            taskNotificationScheduler.scheduleReminderNotification(
                commitmentEntity = commitmentEntity,
                reminderId = remiderId,
                minutesBeforeCommitment = minutesBeforeCommitment,
            )
        }
    }

    fun updateReminder(
        reminderEntity: ReminderEntity,
        startDateTime: Instant,
        minutesBeforeCommitment: Int,
        notificationPermissionLauncher: ActivityResultLauncher<String>,
        scheduleExactAlarmLauncher: ActivityResultLauncher<Intent>,
    ) {
        if (!permissionManager.requestNotificationAndAlarmPermissions(notificationPermissionLauncher, scheduleExactAlarmLauncher)) {
            return
        }

        if (isPastCommitment(startDateTime, minutesBeforeCommitment)) {
            viewModelScope.launch {
                _events.emit(
                    DatabaseUiEvent.ShowError(strings.pastReminderError),
                )
            }
            return
        }

        val updatedReminder =
            ReminderEntity(
                id = reminderEntity.id,
                commitment = reminderEntity.commitment,
                minutesBeforeCommitment = minutesBeforeCommitment,
                createdAt = reminderEntity.createdAt,
                updatedAt = Clock.System.now(),
            )

        viewModelScope.launch {
            reminderRepository.update(updatedReminder)

            val commitmentEntity = commitmentRepository.getCommitment(updatedReminder.commitment)

            if (commitmentEntity == null) {
                _events.emit(DatabaseUiEvent.ShowError(strings.commitmentNotFoundError))
                return@launch
            }

            taskNotificationScheduler.cancelReminderNotification(
                commitmentId = commitmentEntity.id,
                reminderId = updatedReminder.id,
            )
            taskNotificationScheduler.scheduleReminderNotification(
                commitmentEntity = commitmentEntity,
                reminderId = updatedReminder.id,
                minutesBeforeCommitment = minutesBeforeCommitment,
            )
        }
    }

    fun deleteReminder(
        reminderEntity: ReminderEntity,
        notificationPermissionLauncher: ActivityResultLauncher<String>,
        scheduleExactAlarmLauncher: ActivityResultLauncher<Intent>,
    ) {
        if (!permissionManager.requestNotificationAndAlarmPermissions(notificationPermissionLauncher, scheduleExactAlarmLauncher)) {
            return
        }

        viewModelScope.launch {
            reminderRepository.delete(reminderEntity)

            taskNotificationScheduler.cancelReminderNotification(
                commitmentId = reminderEntity.commitment,
                reminderId = reminderEntity.id,
            )
        }
    }

    private fun isPastCommitment(
        startDateTime: Instant,
        minutesBeforeCommitment: Int,
    ): Boolean = (startDateTime - minutesBeforeCommitment.minutes) <= Clock.System.now()
}
