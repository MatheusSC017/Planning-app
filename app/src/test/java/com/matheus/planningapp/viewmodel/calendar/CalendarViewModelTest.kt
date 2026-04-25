package com.matheus.planningapp.viewmodel.calendar

import android.database.sqlite.SQLiteConstraintException
import com.matheus.planningapp.data.calendar.CalendarEntity
import com.matheus.planningapp.data.calendar.CalendarRepository
import com.matheus.planningapp.ui.theme.strings.StringsRepository
import com.matheus.planningapp.ui.theme.strings.StringsRepositoryEnglish
import com.matheus.planningapp.ui.theme.strings.StringsRepositoryPortuguese
import com.matheus.planningapp.ui.theme.strings.StringsRepositorySpanish
import com.matheus.planningapp.util.DatabaseUiEvent
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
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.Locale

@OptIn(ExperimentalCoroutinesApi::class)
class CalendarViewModelTest {
    private lateinit var calendarRepository: CalendarRepository
    private lateinit var strings: StringsRepository
    private lateinit var viewModel: CalendarViewModel
    private val dispatcher = StandardTestDispatcher()
    private val initialCalendars =
        listOf(
            CalendarEntity(id = 1, name = "Calendar 1", isDefault = true),
            CalendarEntity(id = 2, name = "Calendar 2", isDefault = false),
        )

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)

        calendarRepository = mockk<CalendarRepository>()
        strings =
            when (Locale.getDefault().language) {
                "pt" -> StringsRepositoryPortuguese()
                "es" -> StringsRepositorySpanish()
                else -> StringsRepositoryEnglish()
            }
        coEvery { calendarRepository.getCalendars() } returns flowOf(initialCalendars)
        viewModel = CalendarViewModel(calendarRepository, strings)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `insertCalendar with empty name should emit ShowError event`() =
        runTest {
            // Given
            val calendarEntity = CalendarEntity(name = "", isDefault = false)
            val events = mutableListOf<DatabaseUiEvent>()

            // When & Then
            val collectJob =
                launch {
                    viewModel.events.collect {
                        events.add(it)
                    }
                }

            viewModel.insertCalendar(calendarEntity)
            advanceUntilIdle()

            assertEquals(1, events.size)
            assertEquals(
                strings.calendarEmptyNameError,
                (events[0] as DatabaseUiEvent.ShowError).message,
            )

            collectJob.cancel()
        }

    @Test
    fun `insertCalendar with blank name should emit ShowError event`() =
        runTest {
            // Given
            val calendarEntity = CalendarEntity(name = "   ", isDefault = false)
            val events = mutableListOf<DatabaseUiEvent>()

            // When & Then
            val collectJob =
                launch {
                    viewModel.events.collect {
                        events.add(it)
                    }
                }

            viewModel.insertCalendar(calendarEntity)
            advanceUntilIdle()

            assertEquals(1, events.size)
            assertEquals(
                strings.calendarEmptyNameError,
                (events[0] as DatabaseUiEvent.ShowError).message,
            )

            collectJob.cancel()
        }

    @Test
    fun `insertCalendar with valid name should call repository insertCalendar and emit Saved event`() =
        runTest {
            // Given
            val calendarEntity = CalendarEntity(name = "My Calendar", isDefault = false)
            val events = mutableListOf<DatabaseUiEvent>()
            coEvery { calendarRepository.insertCalendar(calendarEntity) } returns 1L

            // When & Then
            val collectJob =
                launch {
                    viewModel.events.collect {
                        events.add(it)
                    }
                }

            viewModel.insertCalendar(calendarEntity)
            advanceUntilIdle()

            coVerify { calendarRepository.insertCalendar(calendarEntity) }
            assertEquals(1, events.size)
            assertEquals(DatabaseUiEvent.Saved, events[0])

            collectJob.cancel()
        }

    @Test
    fun `insertCalendar with default calendar should set all defaults to false first`() =
        runTest {
            // Given
            val calendarEntity = CalendarEntity(name = "Default Calendar", isDefault = true)
            val events = mutableListOf<DatabaseUiEvent>()
            coEvery { calendarRepository.setAllDefaultAsFalse() } returns Unit
            coEvery { calendarRepository.insertCalendar(calendarEntity) } returns 1L

            // When & Then
            val collectJob =
                launch {
                    viewModel.events.collect {
                        events.add(it)
                    }
                }

            viewModel.insertCalendar(calendarEntity)
            advanceUntilIdle()

            coVerify { calendarRepository.setAllDefaultAsFalse() }
            coVerify { calendarRepository.insertCalendar(calendarEntity) }
            assertEquals(1, events.size)
            assertEquals(DatabaseUiEvent.Saved, events[0])

            collectJob.cancel()
        }

    @Test
    fun `insertCalendar with duplicate name should emit ShowError event`() =
        runTest {
            // Given
            val calendarEntity = CalendarEntity(name = "Duplicate Calendar", isDefault = false)
            val events = mutableListOf<DatabaseUiEvent>()
            coEvery { calendarRepository.insertCalendar(calendarEntity) } throws SQLiteConstraintException()

            // When & Then
            val collectJob =
                launch {
                    viewModel.events.collect {
                        events.add(it)
                    }
                }

            viewModel.insertCalendar(calendarEntity)
            advanceUntilIdle()

            assertEquals(1, events.size)
            assertEquals(
                strings.calendarNameMustBeUnique,
                (events[0] as DatabaseUiEvent.ShowError).message,
            )

            collectJob.cancel()
        }

    @Test
    fun `updateCalendar with empty name should emit ShowError event`() =
        runTest {
            // Given
            val calendarEntity = CalendarEntity(id = 1, name = "", isDefault = false)
            val events = mutableListOf<DatabaseUiEvent>()

            // When & Then
            val collectJob =
                launch {
                    viewModel.events.collect {
                        events.add(it)
                    }
                }

            viewModel.updateCalendar(calendarEntity)
            advanceUntilIdle()

            assertEquals(1, events.size)
            assertEquals(
                strings.calendarEmptyNameError,
                (events[0] as DatabaseUiEvent.ShowError).message,
            )

            collectJob.cancel()
        }

    @Test
    fun `updateCalendar with blank name should emit ShowError event`() =
        runTest {
            // Given
            val calendarEntity = CalendarEntity(id = 1, name = "    ", isDefault = false)
            val events = mutableListOf<DatabaseUiEvent>()

            // When & Then
            val collectJob =
                launch {
                    viewModel.events.collect {
                        events.add(it)
                    }
                }

            viewModel.updateCalendar(calendarEntity)
            advanceUntilIdle()

            assertEquals(1, events.size)
            assertEquals(
                strings.calendarEmptyNameError,
                (events[0] as DatabaseUiEvent.ShowError).message,
            )

            collectJob.cancel()
        }

    @Test
    fun `updateCalendar with valid name and non-default calendar should call repository updateCalendar`() =
        runTest {
            // Given
            val calendarEntity = CalendarEntity(id = 1, name = "Updated Calendar", isDefault = false)
            coEvery { calendarRepository.getCalendarById(1L) } returns CalendarEntity(id = 1, name = "Old Name", isDefault = false)
            coEvery { calendarRepository.updateCalendar(calendarEntity) } returns Unit

            // When
            viewModel.updateCalendar(calendarEntity)
            advanceUntilIdle()

            // Then
            coVerify { calendarRepository.updateCalendar(calendarEntity) }
        }

    @Test
    fun `updateCalendar with default calendar should set all defaults to false first`() =
        runTest {
            // Given
            val calendarEntity = CalendarEntity(id = 1, name = "New Default", isDefault = true)
            coEvery { calendarRepository.setAllDefaultAsFalse() } returns Unit
            coEvery { calendarRepository.getCalendarById(1L) } returns CalendarEntity(id = 1, name = "Old Name", isDefault = false)
            coEvery { calendarRepository.updateCalendar(calendarEntity) } returns Unit

            // When
            viewModel.updateCalendar(calendarEntity)
            advanceUntilIdle()

            // Then
            coVerify { calendarRepository.setAllDefaultAsFalse() }
            coVerify { calendarRepository.updateCalendar(calendarEntity) }
        }

    @Test
    fun `updateCalendar removing default status should emit ShowError event`() =
        runTest {
            // Given
            val currentDefault = CalendarEntity(id = 1, name = "Default Calendar", isDefault = true)
            val updatedCalendar = CalendarEntity(id = 1, name = "Default Calendar", isDefault = false)
            val events = mutableListOf<DatabaseUiEvent>()
            coEvery { calendarRepository.getCalendarById(1L) } returns currentDefault

            // When & Then
            val collectJob =
                launch {
                    viewModel.events.collect {
                        events.add(it)
                    }
                }

            viewModel.updateCalendar(updatedCalendar)
            advanceUntilIdle()

            assertEquals(1, events.size)
            assertEquals(
                strings.defaultCalendarCannotBeChanged,
                (events[0] as DatabaseUiEvent.ShowError).message,
            )

            collectJob.cancel()
        }

    @Test
    fun `updateCalendar making a non-default calendar as default should work`() =
        runTest {
            // Given
            val currentNonDefault = CalendarEntity(id = 2, name = "Regular Calendar", isDefault = false)
            val updatedToDefault = CalendarEntity(id = 2, name = "Regular Calendar", isDefault = true)
            coEvery { calendarRepository.setAllDefaultAsFalse() } returns Unit
            coEvery { calendarRepository.getCalendarById(2L) } returns currentNonDefault
            coEvery { calendarRepository.updateCalendar(updatedToDefault) } returns Unit

            // When
            viewModel.updateCalendar(updatedToDefault)
            advanceUntilIdle()

            // Then
            coVerify { calendarRepository.setAllDefaultAsFalse() }
            coVerify { calendarRepository.getCalendarById(2L) }
            coVerify { calendarRepository.updateCalendar(updatedToDefault) }
        }

    @Test
    fun `deleteCalendar with default calendar should emit ShowError event`() =
        runTest {
            // Given
            val defaultCalendar = CalendarEntity(id = 1, name = "Default Calendar", isDefault = true)
            val events = mutableListOf<DatabaseUiEvent>()

            // When & Then
            val collectJob =
                launch {
                    viewModel.events.collect {
                        events.add(it)
                    }
                }

            viewModel.deleteCalendar(defaultCalendar)
            advanceUntilIdle()

            assertEquals(1, events.size)
            assertEquals(
                strings.defaultCalendarCannotBeDeleted,
                (events[0] as DatabaseUiEvent.ShowError).message,
            )

            collectJob.cancel()
        }

    @Test
    fun `deleteCalendar with non-default calendar should call repository deleteCalendar and emit Saved event`() =
        runTest {
            // Given
            val nonDefaultCalendar = CalendarEntity(id = 2, name = "Calendar to Delete", isDefault = false)
            val events = mutableListOf<DatabaseUiEvent>()
            coEvery { calendarRepository.deleteCalendar(nonDefaultCalendar) } returns Unit

            // When & Then
            val collectJob =
                launch {
                    viewModel.events.collect {
                        events.add(it)
                    }
                }

            viewModel.deleteCalendar(nonDefaultCalendar)
            advanceUntilIdle()

            coVerify { calendarRepository.deleteCalendar(nonDefaultCalendar) }
            assertEquals(1, events.size)
            assertEquals(DatabaseUiEvent.Saved, events[0])

            collectJob.cancel()
        }

    @Test
    fun `calendars should emit initial list from repository`() =
        runTest {
            // When & Then
            val collectedCalendars = mutableListOf<List<CalendarEntity>>()
            val collectJob =
                launch {
                    viewModel.calendars.collect { calendars ->
                        collectedCalendars.add(calendars)
                    }
                }

            advanceUntilIdle()

            // StateFlow emits initial empty list first, then the actual data
            assertEquals(2, collectedCalendars.size)
            assertEquals(emptyList<CalendarEntity>(), collectedCalendars[0])
            assertEquals(initialCalendars, collectedCalendars[1])

            collectJob.cancel()
        }
}
