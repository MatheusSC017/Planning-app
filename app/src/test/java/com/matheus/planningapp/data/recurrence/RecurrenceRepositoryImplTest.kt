package com.matheus.planningapp.data.recurrence

import com.matheus.planningapp.util.enums.DayOfWeekEnum
import com.matheus.planningapp.util.enums.FrequencyEnum
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class RecurrenceRepositoryImplTest {

    private lateinit var recurrenceDao: RecurrenceDao
    private lateinit var repository: RecurrenceRepositoryImpl

    @Before
    fun setUp() {
        recurrenceDao = mockk<RecurrenceDao>()
        repository = RecurrenceRepositoryImpl(recurrenceDao)
    }

    @Test
    fun `insert should call dao insert`() = runTest {
        // Given
        val recurrenceEntity = RecurrenceEntity(
            commitment = 1,
            frequency = FrequencyEnum.WEEKLY,
            interval = 1,
            dayOfWeekList = listOf(DayOfWeekEnum.MONDAY),
            dayOfMonth = 1
        )
        coEvery { recurrenceDao.insert(recurrenceEntity) } returns Unit

        // When
        repository.insert(recurrenceEntity)

        // Then
        coVerify { recurrenceDao.insert(recurrenceEntity) }
    }

    @Test
    fun `update should call dao update`() = runTest {
        // Given
        val recurrenceEntity = RecurrenceEntity(
            id = 1,
            commitment = 1,
            frequency = FrequencyEnum.WEEKLY,
            interval = 2,
            dayOfWeekList = listOf(DayOfWeekEnum.TUESDAY),
            dayOfMonth = 1
        )
        coEvery { recurrenceDao.update(recurrenceEntity) } returns Unit

        // When
        repository.update(recurrenceEntity)

        // Then
        coVerify { recurrenceDao.update(recurrenceEntity) }
    }

    @Test
    fun `delete should call dao delete`() = runTest {
        // Given
        val recurrenceEntity = RecurrenceEntity(
            id = 1,
            commitment = 1,
            frequency = FrequencyEnum.WEEKLY,
            interval = 1,
            dayOfWeekList = listOf(DayOfWeekEnum.MONDAY),
            dayOfMonth = 1
        )
        coEvery { recurrenceDao.delete(recurrenceEntity) } returns Unit

        // When
        repository.delete(recurrenceEntity)

        // Then
        coVerify { recurrenceDao.delete(recurrenceEntity) }
    }

    @Test
    fun `getRecurrenceById should call dao getRecurrenceById and return result`() = runTest {
        // Given
        val recurrenceId = 1L
        val recurrenceEntity = RecurrenceEntity(
            id = recurrenceId,
            commitment = 1,
            frequency = FrequencyEnum.WEEKLY,
            interval = 1,
            dayOfWeekList = listOf(DayOfWeekEnum.MONDAY),
            dayOfMonth = 1
        )
        coEvery { recurrenceDao.getRecurrenceById(recurrenceId) } returns recurrenceEntity

        // When
        val result = repository.getRecurrenceById(recurrenceId)

        // Then
        assertEquals(recurrenceEntity, result)
        coVerify { recurrenceDao.getRecurrenceById(recurrenceId) }
    }

    @Test
    fun `getRecurrenceByCalendar should return flow from dao`() = runTest {
        // Given
        val calendarId = 1L
        val recurrences = listOf(
            CommitmentRecurrenceDataClass(
                commitmentId = 1,
                frequency = FrequencyEnum.WEEKLY,
                interval = 0,
                dayOfWeekList = listOf(DayOfWeekEnum.MONDAY),
                dayOfMonth = 0
            ),
            CommitmentRecurrenceDataClass(
                commitmentId = 2,
                frequency = FrequencyEnum.DAILY,
                interval = 0,
                dayOfWeekList = listOf(),
                dayOfMonth = 0
            )
        )
        coEvery { recurrenceDao.getRecurrenceByCalendar(calendarId) } returns flowOf(recurrences)

        // When
        val result = repository.getRecurrenceByCalendar(calendarId)

        // Then
        result.collect { collectedRecurrences ->
            assertEquals(recurrences, collectedRecurrences)
        }
        coVerify { recurrenceDao.getRecurrenceByCalendar(calendarId) }
    }

    @Test
    fun `getRecurrenceByCommitment should call dao getRecurrenceByCommitment and return result`() = runTest {
        // Given
        val commitmentId = 1L
        val recurrenceEntity = RecurrenceEntity(
            id = 1,
            commitment = commitmentId,
            frequency = FrequencyEnum.WEEKLY,
            interval = 1,
            dayOfWeekList = listOf(DayOfWeekEnum.MONDAY),
            dayOfMonth = 1
        )
        coEvery { recurrenceDao.getRecurrenceByCommitment(commitmentId) } returns recurrenceEntity

        // When
        val result = repository.getRecurrenceByCommitment(commitmentId)

        // Then
        assertEquals(recurrenceEntity, result)
        coVerify { recurrenceDao.getRecurrenceByCommitment(commitmentId) }
    }
}

