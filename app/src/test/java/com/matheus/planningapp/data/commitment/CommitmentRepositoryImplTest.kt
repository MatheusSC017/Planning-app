package com.matheus.planningapp.data.commitment

import com.matheus.planningapp.util.enums.DayOfWeekEnum
import com.matheus.planningapp.util.enums.PriorityEnum
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CommitmentRepositoryImplTest {
    private lateinit var commitmentDao: CommitmentDao
    private lateinit var repository: CommitmentRepositoryImpl

    @Before
    fun setUp() {
        commitmentDao = mockk<CommitmentDao>()
        repository = CommitmentRepositoryImpl(commitmentDao)
    }

    @Test
    fun `getCommitment should call dao getCommitment and return result`() =
        runTest {
            // Given
            val commitmentId = 1L
            val commitmentEntity =
                CommitmentEntity(
                    id = commitmentId,
                    calendar = 1,
                    title = "Test Commitment",
                    description = "Test Description",
                    startDateTime = Instant.parse("2024-01-01T10:00:00Z"),
                    endDateTime = Instant.parse("2024-01-01T11:00:00Z"),
                    priorityEnum = PriorityEnum.HIGH,
                )
            coEvery { commitmentDao.getCommitment(commitmentId) } returns commitmentEntity

            // When
            val result = repository.getCommitment(commitmentId)

            // Then
            assertEquals(commitmentEntity, result)
            coVerify { commitmentDao.getCommitment(commitmentId) }
        }

    @Test
    fun `getCommitmentsForDay should return flow from dao`() =
        runTest {
            // Given
            val dayStart = Instant.parse("2024-01-01T00:00:00Z")
            val dayEnd = Instant.parse("2024-01-01T23:59:59Z")
            val calendarId = 1L
            val commitments =
                listOf(
                    CommitmentEntity(
                        id = 1,
                        calendar = calendarId,
                        title = "Commitment 1",
                        description = "Description 1",
                        startDateTime = dayStart,
                        endDateTime = dayStart,
                        priorityEnum = PriorityEnum.HIGH,
                    ),
                    CommitmentEntity(
                        id = 2,
                        calendar = calendarId,
                        title = "Commitment 2",
                        description = "Description 2",
                        startDateTime = dayStart,
                        endDateTime = dayEnd,
                        priorityEnum = PriorityEnum.LOW,
                    ),
                )
            coEvery { commitmentDao.getCommitmentsForDay(dayStart, dayEnd, calendarId) } returns flowOf(commitments)

            // When
            val result = repository.getCommitmentsForDay(dayStart, dayEnd, calendarId)

            // Then
            result.collect { collectedCommitments ->
                assertEquals(commitments, collectedCommitments)
            }
            coVerify { commitmentDao.getCommitmentsForDay(dayStart, dayEnd, calendarId) }
        }

    @Test
    fun `insertCommitment should call dao insert and return id`() =
        runTest {
            // Given
            val commitmentEntity =
                CommitmentEntity(
                    calendar = 1,
                    title = "Test Commitment",
                    description = "Test Description",
                    startDateTime = Instant.parse("2024-01-01T10:00:00Z"),
                    endDateTime = Instant.parse("2024-01-01T11:00:00Z"),
                    priorityEnum = PriorityEnum.MEDIUM,
                )
            val expectedId = 1L
            coEvery { commitmentDao.insert(commitmentEntity) } returns expectedId

            // When
            val result = repository.insertCommitment(commitmentEntity)

            // Then
            assertEquals(expectedId, result)
            coVerify { commitmentDao.insert(commitmentEntity) }
        }

    @Test
    fun `updateCommitment should call dao update`() =
        runTest {
            // Given
            val commitmentEntity =
                CommitmentEntity(
                    id = 1,
                    title = "Updated Commitment",
                    description = "Updated Description",
                    calendar = 1,
                    startDateTime = Instant.parse("2024-01-01T10:00:00Z"),
                    endDateTime = Instant.parse("2024-01-01T11:00:00Z"),
                    priorityEnum = PriorityEnum.LOW,
                )
            coEvery { commitmentDao.update(commitmentEntity) } returns Unit

            // When
            repository.updateCommitment(commitmentEntity)

            // Then
            coVerify { commitmentDao.update(commitmentEntity) }
        }

    @Test
    fun `deleteCommitment should call dao delete`() =
        runTest {
            // Given
            val commitmentEntity =
                CommitmentEntity(
                    id = 1,
                    title = "Commitment to Delete",
                    description = "To Delete",
                    calendar = 1,
                    startDateTime = Instant.parse("2024-01-01T10:00:00Z"),
                    endDateTime = Instant.parse("2024-01-01T11:00:00Z"),
                    priorityEnum = PriorityEnum.HIGH,
                )
            coEvery { commitmentDao.delete(commitmentEntity) } returns Unit

            // When
            repository.deleteCommitment(commitmentEntity)

            // Then
            coVerify { commitmentDao.delete(commitmentEntity) }
        }

    @Test
    fun `checkSchedulingConflictsBetweenCommitments should call dao with correct parameters`() =
        runTest {
            // Given
            val startDateTime = Instant.parse("2024-01-01T10:00:00Z")
            val endDateTime = Instant.parse("2024-01-01T11:00:00Z")
            val calendarId = 1L
            val commitmentId = 1L
            val expectedConflictCount = 2
            coEvery { commitmentDao.checkSchedulingConflictsBetweenCommitments(any(), any(), any(), any(), any(), any()) } returns
                expectedConflictCount

            // When
            val result = repository.checkSchedulingConflictsBetweenCommitments(startDateTime, endDateTime, calendarId, commitmentId)

            // Then
            assertEquals(expectedConflictCount, result)
            coVerify { commitmentDao.checkSchedulingConflictsBetweenCommitments(any(), any(), any(), any(), any(), any()) }
        }

    @Test
    fun `getFutureCommitments should call dao getFutureCommitments and return result`() =
        runTest {
            // Given
            val futureCommitments =
                listOf(
                    CommitmentEntity(
                        id = 1,
                        title = "Future Commitment 1",
                        description = "Description 1",
                        calendar = 1,
                        startDateTime = Instant.parse("2024-12-01T10:00:00Z"),
                        endDateTime = Instant.parse("2024-12-01T11:00:00Z"),
                        priorityEnum = PriorityEnum.MEDIUM,
                    ),
                    CommitmentEntity(
                        id = 2,
                        title = "Future Commitment 2",
                        description = "Description 2",
                        calendar = 1,
                        startDateTime = Instant.parse("2024-12-02T10:00:00Z"),
                        endDateTime = Instant.parse("2024-12-02T11:00:00Z"),
                        priorityEnum = PriorityEnum.HIGH,
                    ),
                )
            coEvery { commitmentDao.getFutureCommitments(any()) } returns futureCommitments

            // When
            val result = repository.getFutureCommitments()

            // Then
            assertEquals(futureCommitments, result)
            coVerify { commitmentDao.getFutureCommitments(any()) }
        }

    @Test
    fun `getCommitmentByRecurrence should return flow from dao`() =
        runTest {
            // Given
            val calendarId = 1L
            val today = Instant.parse("2024-01-01T00:00:00Z")
            val dayOfWeek = DayOfWeekEnum.MONDAY
            val dayOfMonth = 1
            val commitments =
                listOf(
                    CommitmentEntity(
                        id = 1,
                        title = "Recurring Commitment 1",
                        description = "Description 1",
                        calendar = calendarId,
                        startDateTime = today,
                        endDateTime = today,
                        priorityEnum = PriorityEnum.LOW,
                    ),
                    CommitmentEntity(
                        id = 2,
                        title = "Recurring Commitment 2",
                        description = "Description 2",
                        calendar = calendarId,
                        startDateTime = today,
                        endDateTime = today,
                        priorityEnum = PriorityEnum.HIGH,
                    ),
                )
            coEvery { commitmentDao.getCommitmentByRecurrence(calendarId, today, dayOfWeek, dayOfMonth) } returns flowOf(commitments)

            // When
            val result = repository.getCommitmentByRecurrence(calendarId, today, dayOfWeek, dayOfMonth)

            // Then
            result.collect { collectedCommitments ->
                assertEquals(commitments, collectedCommitments)
            }
            coVerify { commitmentDao.getCommitmentByRecurrence(calendarId, today, dayOfWeek, dayOfMonth) }
        }
}
