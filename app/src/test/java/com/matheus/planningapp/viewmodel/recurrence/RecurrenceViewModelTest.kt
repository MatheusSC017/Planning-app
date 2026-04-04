package com.matheus.planningapp.viewmodel.recurrence

import com.matheus.planningapp.data.calendar.CalendarEntity
import com.matheus.planningapp.data.calendar.CalendarRepository
import com.matheus.planningapp.data.recurrence.CommitmentRecurrenceDataClass
import com.matheus.planningapp.data.recurrence.RecurrenceEntity
import com.matheus.planningapp.data.recurrence.RecurrenceRepository
import com.matheus.planningapp.util.enums.DayOfWeekEnum
import com.matheus.planningapp.util.enums.FrequencyEnum
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
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RecurrenceViewModelTest {

    private lateinit var calendarRepository: CalendarRepository
    private lateinit var recurrenceRepository: RecurrenceRepository
    private lateinit var viewModel: RecurrenceViewModel
    private val dispatcher = StandardTestDispatcher()
    private val initialCalendars = listOf(
        CalendarEntity(id = 1, name = "Calendar 1", isDefault = true),
        CalendarEntity(id = 2, name = "Calendar 2", isDefault = false)
    )
    private val initialRecurrences = listOf(
        CommitmentRecurrenceDataClass(
            commitmentId = 1L,
            recurrenceId = 1L,
            title = "Daily Task",
            startDateTime = Clock.System.now(),
            endDateTime = Clock.System.now(),
            frequency = FrequencyEnum.DAILY,
            dayOfWeekList = emptyList(),
            dayOfMonth = 1,
            interval = 1
        ),
        CommitmentRecurrenceDataClass(
            commitmentId = 2L,
            recurrenceId = 2L,
            title = "Weekly Task",
            startDateTime = Clock.System.now(),
            endDateTime = Clock.System.now(),
            frequency = FrequencyEnum.WEEKLY,
            dayOfWeekList = listOf(DayOfWeekEnum.MONDAY, DayOfWeekEnum.WEDNESDAY, DayOfWeekEnum.FRIDAY),
            dayOfMonth = 1,
            interval = 1
        )
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)

        calendarRepository = mockk<CalendarRepository>()
        recurrenceRepository = mockk<RecurrenceRepository>()
        coEvery { calendarRepository.getCalendars() } returns flowOf(initialCalendars)
        viewModel = RecurrenceViewModel(calendarRepository, recurrenceRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getRecurrencesByCalendar should return flow of recurrences for calendar`() = runTest {
        // Given
        val calendarId = 1L
        coEvery { recurrenceRepository.getRecurrenceByCalendar(calendarId) } returns flowOf(initialRecurrences)
        val collectedRecurrences = mutableListOf<List<CommitmentRecurrenceDataClass>>()

        // When
        val collectJob = launch {
            viewModel.getRecurrencesByCalendar(calendarId).collect { recurrences ->
                collectedRecurrences.add(recurrences)
            }
        }

        advanceUntilIdle()

        // Then
        assertEquals(1, collectedRecurrences.size)
        assertEquals(initialRecurrences, collectedRecurrences[0])

        collectJob.cancel()
    }

    @Test
    fun `getRecurrencesByCalendar should return empty list when no recurrences exist`() = runTest {
        // Given
        val calendarId = 1L
        coEvery { recurrenceRepository.getRecurrenceByCalendar(calendarId) } returns flowOf(emptyList())
        val collectedRecurrences = mutableListOf<List<CommitmentRecurrenceDataClass>>()

        // When
        val collectJob = launch {
            viewModel.getRecurrencesByCalendar(calendarId).collect { recurrences ->
                collectedRecurrences.add(recurrences)
            }
        }

        advanceUntilIdle()

        // Then
        assertEquals(1, collectedRecurrences.size)
        assertEquals(emptyList<CommitmentRecurrenceDataClass>(), collectedRecurrences[0])

        collectJob.cancel()
    }

    @Test
    fun `deleteRecurrence with existing recurrence should call repository delete`() = runTest {
        // Given
        val recurrenceId = 1L
        val recurrenceEntity = RecurrenceEntity(
            id = recurrenceId,
            commitment = 1L,
            frequency = FrequencyEnum.DAILY,
            interval = 1,
            dayOfWeekList = emptyList(),
            dayOfMonth = 1
        )
        coEvery { recurrenceRepository.getRecurrenceById(recurrenceId) } returns recurrenceEntity
        coEvery { recurrenceRepository.delete(recurrenceEntity) } returns Unit

        // When
        viewModel.deleteRecurrence(recurrenceId)
        advanceUntilIdle()

        // Then
        coVerify { recurrenceRepository.getRecurrenceById(recurrenceId) }
        coVerify { recurrenceRepository.delete(recurrenceEntity) }
    }

    @Test
    fun `deleteRecurrence with non-existing recurrence should not call repository delete`() = runTest {
        // Given
        val recurrenceId = 999L
        coEvery { recurrenceRepository.getRecurrenceById(recurrenceId) } returns null

        // When
        viewModel.deleteRecurrence(recurrenceId)
        advanceUntilIdle()

        // Then
        coVerify { recurrenceRepository.getRecurrenceById(recurrenceId) }
        coVerify(exactly = 0) { recurrenceRepository.delete(any()) }
    }

    @Test
    fun `uiState should emit initial state with calendars from repository`() = runTest {
        // When & Then
        val collectedStates = mutableListOf<RecurrenceUiState>()
        val collectJob = launch {
            viewModel.uiState.collect { state ->
                collectedStates.add(state)
            }
        }

        advanceUntilIdle()

        // StateFlow emits initial empty state first, then the actual data
        assertEquals(2, collectedStates.size)
        assertEquals(RecurrenceUiState(), collectedStates[0])
        assertEquals(RecurrenceUiState(calendars = initialCalendars), collectedStates[1])

        collectJob.cancel()
    }

    @Test
    fun `uiState should update when calendars change`() = runTest {
        // Given
        val newCalendars = listOf(
            CalendarEntity(id = 3, name = "New Calendar", isDefault = false)
        )
        val updatedCalendarFlow = flowOf(newCalendars)
        coEvery { calendarRepository.getCalendars() } returns updatedCalendarFlow

        // Create a new view model with updated repository
        val newViewModel = RecurrenceViewModel(calendarRepository, recurrenceRepository)

        // When & Then
        val collectedStates = mutableListOf<RecurrenceUiState>()
        val collectJob = launch {
            newViewModel.uiState.collect { state ->
                collectedStates.add(state)
            }
        }

        advanceUntilIdle()

        // Verify that the state eventually contains the new calendars
        val statesWithCalendars = collectedStates.filter { it.calendars.isNotEmpty() }
        assertEquals(true, statesWithCalendars.any { it.calendars == newCalendars })

        collectJob.cancel()
    }
}

