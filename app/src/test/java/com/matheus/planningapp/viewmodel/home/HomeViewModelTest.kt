package com.matheus.planningapp.viewmodel.home

import android.content.Context
import com.matheus.planningapp.data.calendar.CalendarEntity
import com.matheus.planningapp.data.calendar.CalendarRepository
import com.matheus.planningapp.data.commitment.CommitmentEntity
import com.matheus.planningapp.data.commitment.CommitmentRepository
import com.matheus.planningapp.data.reminder.ReminderRepository
import com.matheus.planningapp.datastore.SettingsRepository
import com.matheus.planningapp.util.enums.NotificationEnum
import com.matheus.planningapp.util.enums.PriorityEnum
import com.matheus.planningapp.util.enums.ViewEnum
import com.matheus.planningapp.util.notification.TaskNotificationScheduler
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Clock
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private lateinit var context: Context
    private lateinit var calendarRepository: CalendarRepository
    private lateinit var commitmentRepository: CommitmentRepository
    private lateinit var reminderRepository: ReminderRepository
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var taskNotificationScheduler: TaskNotificationScheduler
    private lateinit var viewModel: HomeViewModel
    private val dispatcher = StandardTestDispatcher()

    private val initialCalendars =
        listOf(
            CalendarEntity(id = 1, name = "Calendar 1", isDefault = true),
            CalendarEntity(id = 2, name = "Calendar 2", isDefault = false),
        )

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)

        context = mockk<Context>()
        calendarRepository = mockk<CalendarRepository>()
        commitmentRepository = mockk<CommitmentRepository>()
        reminderRepository = mockk<ReminderRepository>()
        settingsRepository = mockk<SettingsRepository>()
        taskNotificationScheduler = mockk<TaskNotificationScheduler>()

        coEvery { calendarRepository.getCalendars() } returns flowOf(initialCalendars)
        coEvery { calendarRepository.ensureDefaultCalendarExists() } returns Unit
        coEvery { settingsRepository.viewModeFlow } returns flowOf(ViewEnum.COLUMN)
        coEvery { settingsRepository.notificationOptionFlow } returns flowOf(NotificationEnum.NO_SEND)

        viewModel =
            HomeViewModel(
                context,
                calendarRepository,
                commitmentRepository,
                reminderRepository,
                settingsRepository,
                taskNotificationScheduler,
            )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState should emit initial state with calendars from repository`() =
        runTest {
            // When
            val collectedStates = mutableListOf<HomeUiState>()
            val collectJob =
                launch {
                    viewModel.uiState.collect { state ->
                        collectedStates.add(state)
                    }
                }
            advanceUntilIdle()

            // Then
            // StateFlow emits initial empty list first, then the actual data
            assertEquals(2, collectedStates.size)
            assertEquals(emptyList<CalendarEntity>(), collectedStates[0].calendars)
            assertEquals(initialCalendars, collectedStates[1].calendars)
            assertEquals(ViewEnum.COLUMN, collectedStates[1].viewMode)
            assertEquals(NotificationEnum.NO_SEND, collectedStates[1].notificationOption)

            collectJob.cancel()
        }

    @Test
    fun `onSelectedDate with valid date should update selected date`() =
        runTest {
            // Given
            val testDate = LocalDate.of(2025, 6, 15)
            val collectedStates = mutableListOf<HomeUiState>()

            val collectJob =
                launch {
                    viewModel.uiState.collect { state ->
                        collectedStates.add(state)
                    }
                }
            advanceUntilIdle()
            val initialStateSize = collectedStates.size

            // When
            viewModel.onSelectedDate(year = 2025, month = 6, day = 15)
            advanceUntilIdle()

            // Then
            assertTrue(collectedStates.size > initialStateSize)
            assertEquals(testDate, collectedStates.last().selectedDate)

            collectJob.cancel()
        }

    @Test
    fun `onSelectedDate with null values should keep current values`() =
        runTest {
            // Given
            val collectedStates = mutableListOf<HomeUiState>()
            val collectJob =
                launch {
                    viewModel.uiState.collect { state ->
                        collectedStates.add(state)
                    }
                }
            advanceUntilIdle()

            viewModel.onSelectedDate(year = 2025, month = 3, day = 10)
            advanceUntilIdle()

            val stateBeforePartialUpdate = collectedStates.last().selectedDate

            // When
            viewModel.onSelectedDate(year = null, month = null, day = null)
            advanceUntilIdle()

            // Then
            assertEquals(stateBeforePartialUpdate, collectedStates.last().selectedDate)

            collectJob.cancel()
        }

    @Test
    fun `onSelectedDate should coerce day to valid range for month`() =
        runTest {
            // Given - February has 28 days in 2025
            val collectedStates = mutableListOf<HomeUiState>()

            // When
            val collectJob =
                launch {
                    viewModel.uiState.collect { state ->
                        collectedStates.add(state)
                    }
                }
            advanceUntilIdle()

            // Try to set day 31 on February 2025 (should be coerced to 28)
            viewModel.onSelectedDate(year = 2025, month = 2, day = 31)
            advanceUntilIdle()

            // Then
            assertEquals(LocalDate.of(2025, 2, 28), collectedStates.last().selectedDate)

            collectJob.cancel()
        }

    @Test
    fun `onSelectedDate with year only should update year and keep month and day`() =
        runTest {
            // Given
            val collectedStates = mutableListOf<HomeUiState>()

            val collectJob =
                launch {
                    viewModel.uiState.collect { state ->
                        collectedStates.add(state)
                    }
                }

            advanceUntilIdle()
            val initialDate = collectedStates.last().selectedDate

            // When
            viewModel.onSelectedDate(year = 2030)
            advanceUntilIdle()

            // Then
            val updatedDate = collectedStates.last().selectedDate
            assertEquals(2030, updatedDate.year)
            assertEquals(initialDate.monthValue, updatedDate.monthValue)
            assertEquals(initialDate.dayOfMonth, updatedDate.dayOfMonth)

            collectJob.cancel()
        }

    @Test
    fun `incrementYear should increase year by one`() =
        runTest {
            // Given
            val collectedStates = mutableListOf<HomeUiState>()

            val collectJob =
                launch {
                    viewModel.uiState.collect { state ->
                        collectedStates.add(state)
                    }
                }
            advanceUntilIdle()

            val initialYear = collectedStates.last().selectedDate.year

            // When
            viewModel.incrementYear()
            advanceUntilIdle()

            // Then
            assertEquals(initialYear + 1, collectedStates.last().selectedDate.year)

            collectJob.cancel()
        }

    @Test
    fun `decrementYear should decrease year by one`() =
        runTest {
            // Given
            val collectedStates = mutableListOf<HomeUiState>()

            val collectJob =
                launch {
                    viewModel.uiState.collect { state ->
                        collectedStates.add(state)
                    }
                }
            advanceUntilIdle()

            val initialYear = collectedStates.last().selectedDate.year

            // When
            viewModel.decrementYear()
            advanceUntilIdle()

            // Then
            assertEquals(initialYear - 1, collectedStates.last().selectedDate.year)

            collectJob.cancel()
        }

    @Test
    fun `getCommitmentsForDay should combine daily and recurrence commitments`() =
        runTest {
            // Given
            val dayStart = Clock.System.now()
            val dayEnd = Clock.System.now().plus(kotlin.time.Duration.parse("1d"))
            val calendarId = 1L

            val dailyCommitments =
                listOf(
                    CommitmentEntity(
                        id = 1,
                        calendar = calendarId,
                        title = "Morning Meeting",
                        description = null,
                        startDateTime = dayStart,
                        endDateTime = dayStart.plus(kotlin.time.Duration.parse("1h")),
                        priorityEnum = PriorityEnum.HIGH,
                    ),
                )

            val recurrenceCommitments =
                listOf(
                    CommitmentEntity(
                        id = 2,
                        calendar = calendarId,
                        title = "Daily Standup",
                        description = null,
                        startDateTime = dayStart.plus(kotlin.time.Duration.parse("2h")),
                        endDateTime = dayStart.plus(kotlin.time.Duration.parse("3h")),
                        priorityEnum = PriorityEnum.MEDIUM,
                    ),
                )

            coEvery { commitmentRepository.getCommitmentsForDay(dayStart, dayEnd, calendarId) } returns flowOf(dailyCommitments)
            coEvery {
                commitmentRepository.getCommitmentByRecurrence(
                    calendar = calendarId,
                    today = dayStart,
                    dayOfWeek = any(),
                    dayOfMonth = any(),
                )
            } returns flowOf(recurrenceCommitments)

            // When
            val commitments = mutableListOf<List<CommitmentEntity>>()
            val collectJob =
                launch {
                    viewModel.getCommitmentsForDay(dayStart, dayEnd, calendarId).collect {
                        commitments.add(it)
                    }
                }

            advanceUntilIdle()

            // Then
            assertEquals(1, commitments.size)
            assertEquals(2, commitments[0].size)
            assertTrue(commitments[0].contains(dailyCommitments[0]))
            assertTrue(commitments[0].contains(recurrenceCommitments[0]))

            collectJob.cancel()
        }

    @Test
    fun `getCommitmentsForDay should return empty list when no commitments exist`() =
        runTest {
            // Given
            val dayStart = Clock.System.now()
            val dayEnd = dayStart.plus(kotlin.time.Duration.parse("1d"))
            val calendarId = 1L

            coEvery { commitmentRepository.getCommitmentsForDay(dayStart, dayEnd, calendarId) } returns flowOf(emptyList())
            coEvery {
                commitmentRepository.getCommitmentByRecurrence(
                    calendar = calendarId,
                    today = dayStart,
                    dayOfWeek = any(),
                    dayOfMonth = any(),
                )
            } returns flowOf(emptyList())

            // When
            val commitments = mutableListOf<List<CommitmentEntity>>()
            val collectJob =
                launch {
                    viewModel.getCommitmentsForDay(dayStart, dayEnd, calendarId).collect {
                        commitments.add(it)
                    }
                }

            advanceUntilIdle()

            // Then
            assertEquals(1, commitments.size)
            assertEquals(0, commitments[0].size)

            collectJob.cancel()
        }

    @Test
    fun `deleteCommitment should call repository deleteCommitment`() =
        runTest {
            // Given
            val commitmentToDelete =
                CommitmentEntity(
                    id = 1,
                    calendar = 1,
                    title = "Task to Delete",
                    description = null,
                    startDateTime = Clock.System.now(),
                    endDateTime = Clock.System.now().plus(kotlin.time.Duration.parse("1h")),
                    priorityEnum = PriorityEnum.LOW,
                )
            coEvery { commitmentRepository.deleteCommitment(commitmentToDelete) } returns Unit

            // When
            viewModel.deleteCommitment(commitmentToDelete)
            advanceUntilIdle()

            // Then
            coVerify { commitmentRepository.deleteCommitment(commitmentToDelete) }
        }

    @Test
    fun `init should ensure default calendar exists on creation`() =
        runTest {
            // Given
            coEvery { calendarRepository.ensureDefaultCalendarExists() } returns Unit

            // Then
            coVerify { calendarRepository.ensureDefaultCalendarExists() }
        }

    // TODO: Include test to reminder methods
}
