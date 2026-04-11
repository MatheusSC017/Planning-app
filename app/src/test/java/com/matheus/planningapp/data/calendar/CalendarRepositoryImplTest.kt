package com.matheus.planningapp.data.calendar

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CalendarRepositoryImplTest {
    private lateinit var calendarDao: CalendarDao
    private lateinit var repository: CalendarRepositoryImpl

    @Before
    fun setUp() {
        calendarDao = mockk<CalendarDao>()
        repository = CalendarRepositoryImpl(calendarDao)
    }

    @Test
    fun `insertCalendar should call dao insert and return id`() =
        runTest {
            // Given
            val calendarEntity = CalendarEntity(name = "Test Calendar", isDefault = false)
            val expectedId = 1L
            coEvery { calendarDao.insert(calendarEntity) } returns expectedId

            // When
            val result = repository.insertCalendar(calendarEntity)

            // Then
            assertEquals(expectedId, result)
            coVerify { calendarDao.insert(calendarEntity) }
        }

    @Test
    fun `updateCalendar should call dao updateWithDateTime`() =
        runTest {
            // Given
            val calendarEntity = CalendarEntity(id = 1, name = "Updated Calendar", isDefault = true)
            coEvery { calendarDao.updateWithDateTime(calendarEntity) } returns Unit

            // When
            repository.updateCalendar(calendarEntity)

            // Then
            coVerify { calendarDao.updateWithDateTime(calendarEntity) }
        }

    @Test
    fun `deleteCalendar should call dao delete`() =
        runTest {
            // Given
            val calendarEntity = CalendarEntity(id = 1, name = "Calendar to Delete", isDefault = false)
            coEvery { calendarDao.delete(calendarEntity) } returns Unit

            // When
            repository.deleteCalendar(calendarEntity)

            // Then
            coVerify { calendarDao.delete(calendarEntity) }
        }

    @Test
    fun `getCalendars should return flow from dao`() =
        runTest {
            // Given
            val calendars =
                listOf(
                    CalendarEntity(id = 1, name = "Calendar 1", isDefault = true),
                    CalendarEntity(id = 2, name = "Calendar 2", isDefault = false),
                )
            coEvery { calendarDao.getCalendars() } returns flowOf(calendars)

            // When
            val result = repository.getCalendars()

            // Then
            result.collect { collectedCalendars ->
                assertEquals(calendars, collectedCalendars)
            }
            coVerify { calendarDao.getCalendars() }
        }

    @Test
    fun `getCalendarById should call dao getCalendarById and return result`() =
        runTest {
            // Given
            val calendarId = 1L
            val calendarEntity = CalendarEntity(id = calendarId, name = "Test Calendar", isDefault = false)
            coEvery { calendarDao.getCalendarById(calendarId) } returns calendarEntity

            // When
            val result = repository.getCalendarById(calendarId)

            // Then
            assertEquals(calendarEntity, result)
            coVerify { calendarDao.getCalendarById(calendarId) }
        }

    @Test
    fun `ensureDefaultCalendarExists should insert default calendar if count is zero`() =
        runTest {
            // Given
            coEvery { calendarDao.countCalendars() } returns 0
            coEvery { calendarDao.insert(any()) } returns 1L

            // When
            repository.ensureDefaultCalendarExists()

            // Then
            coVerify { calendarDao.countCalendars() }
            coVerify { calendarDao.insert(match { it.name == "Default" && it.isDefault }) }
        }

    @Test
    fun `ensureDefaultCalendarExists should not insert if count is greater than zero`() =
        runTest {
            // Given
            coEvery { calendarDao.countCalendars() } returns 1

            // When
            repository.ensureDefaultCalendarExists()

            // Then
            coVerify { calendarDao.countCalendars() }
            coVerify(exactly = 0) { calendarDao.insert(any()) }
        }

    @Test
    fun `setAllDefaultAsFalse should call dao setAllDefaultAsFalse`() =
        runTest {
            // Given
            coEvery { calendarDao.setAllDefaultAsFalse() } returns Unit

            // When
            repository.setAllDefaultAsFalse()

            // Then
            coVerify { calendarDao.setAllDefaultAsFalse() }
        }
}
