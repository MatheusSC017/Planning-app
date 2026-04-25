package com.matheus.planningapp.viewmodel.commitment

import com.matheus.planningapp.data.commitment.CommitmentEntity
import com.matheus.planningapp.data.commitment.CommitmentRepository
import com.matheus.planningapp.data.recurrence.RecurrenceEntity
import com.matheus.planningapp.data.recurrence.RecurrenceRepository
import com.matheus.planningapp.datastore.SettingsRepository
import com.matheus.planningapp.ui.theme.strings.StringsRepository
import com.matheus.planningapp.ui.theme.strings.StringsRepositoryEnglish
import com.matheus.planningapp.ui.theme.strings.StringsRepositoryPortuguese
import com.matheus.planningapp.ui.theme.strings.StringsRepositorySpanish
import com.matheus.planningapp.util.DatabaseUiEvent
import com.matheus.planningapp.util.enums.DayOfWeekEnum
import com.matheus.planningapp.util.enums.FrequencyEnum
import com.matheus.planningapp.util.enums.NotificationEnum
import com.matheus.planningapp.util.enums.PriorityEnum
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
import org.junit.Before
import org.junit.Test
import java.util.Locale
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@OptIn(ExperimentalCoroutinesApi::class)
class CommitmentFormViewModelTest {
    private lateinit var commitmentRepository: CommitmentRepository
    private lateinit var recurrenceRepository: RecurrenceRepository
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var taskNotificationScheduler: TaskNotificationScheduler
    private lateinit var strings: StringsRepository
    private lateinit var viewModel: CommitmentFormViewModel
    private val dispatcher = StandardTestDispatcher()

    private val testNow = Clock.System.now()
    private val testInstant = testNow + 1.hours
    private val testEndInstant = testInstant + 30.minutes
    private val calendarId = 1L

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)

        commitmentRepository = mockk<CommitmentRepository>()
        recurrenceRepository = mockk<RecurrenceRepository>()
        settingsRepository = mockk<SettingsRepository>()
        taskNotificationScheduler = mockk<TaskNotificationScheduler>(relaxed = true)
        strings =
            when (Locale.getDefault().language) {
                "pt" -> StringsRepositoryPortuguese()
                "es" -> StringsRepositorySpanish()
                else -> StringsRepositoryEnglish()
            }

        coEvery { settingsRepository.notificationOptionFlow } returns flowOf(NotificationEnum.NO_SEND)
        coEvery { recurrenceRepository.getRecurrenceByCommitment(any()) } returns null
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModelInCreateMode() {
        viewModel =
            CommitmentFormViewModel(
                CommitmentFormMode.Create(calendarId, testInstant),
                commitmentRepository,
                settingsRepository,
                recurrenceRepository,
                taskNotificationScheduler,
                strings,
            )
    }

    private fun createViewModelInEditMode(commitmentId: Long = 1L) {
        coEvery { commitmentRepository.getCommitment(commitmentId) } returns
            CommitmentEntity(
                id = commitmentId,
                calendar = calendarId,
                title = "Test Commitment",
                description = "Test Description",
                startDateTime = testInstant,
                endDateTime = testEndInstant,
                priorityEnum = PriorityEnum.MEDIUM,
            )

        viewModel =
            CommitmentFormViewModel(
                CommitmentFormMode.Edit(commitmentId),
                commitmentRepository,
                settingsRepository,
                recurrenceRepository,
                taskNotificationScheduler,
                strings,
            )
    }

    @Test
    fun `commitmentFormViewModel in create mode should initialize with create parameters`() =
        runTest {
            // When
            createViewModelInCreateMode()

            val collectJob =
                launch {
                    viewModel.commitmentUiState.collect { }
                }
            advanceUntilIdle()

            // Then
            assertEquals(calendarId, viewModel.commitmentUiState.value.calendarId)
            assertEquals(testInstant, viewModel.commitmentUiState.value.startInstant)
            assertEquals(testInstant + 30.minutes, viewModel.commitmentUiState.value.endInstant)
            assertEquals("", viewModel.commitmentUiState.value.title)

            collectJob.cancel()
        }

    @Test
    fun `commitmentFormViewModel in edit mode should load commitment data`() =
        runTest {
            // Given
            val commitmentId = 1L
            val testCommitment =
                CommitmentEntity(
                    id = commitmentId,
                    calendar = calendarId,
                    title = "Existing Commitment",
                    description = "Existing Description",
                    startDateTime = testInstant,
                    endDateTime = testEndInstant,
                    priorityEnum = PriorityEnum.HIGH,
                )
            coEvery { commitmentRepository.getCommitment(commitmentId) } returns testCommitment

            // When
            viewModel =
                CommitmentFormViewModel(
                    CommitmentFormMode.Edit(commitmentId),
                    commitmentRepository,
                    settingsRepository,
                    recurrenceRepository,
                    taskNotificationScheduler,
                    strings,
                )

            val collectJob =
                launch {
                    viewModel.commitmentUiState.collect { }
                }
            advanceUntilIdle()

            // Then
            assertEquals(commitmentId, viewModel.commitmentUiState.value.id)
            assertEquals("Existing Commitment", viewModel.commitmentUiState.value.title)
            assertEquals("Existing Description", viewModel.commitmentUiState.value.description)
            assertEquals(PriorityEnum.HIGH, viewModel.commitmentUiState.value.priorityEnum)

            collectJob.cancel()
        }

    @Test
    fun `commitmentFormViewModel in edit mode with not found commitment should emit error`() =
        runTest {
            // Given
            val commitmentId = 1L
            val events = mutableListOf<DatabaseUiEvent>()
            coEvery { commitmentRepository.getCommitment(commitmentId) } returns null

            // When
            val collectJob =
                launch {
                    val viewModel =
                        CommitmentFormViewModel(
                            CommitmentFormMode.Edit(commitmentId),
                            commitmentRepository,
                            settingsRepository,
                            recurrenceRepository,
                            taskNotificationScheduler,
                            strings,
                        )
                    viewModel.events.collect { events.add(it) }
                }
            advanceUntilIdle()

            // Then
            assertEquals(1, events.size)
            assertEquals(strings.commitmentNotFoundError, (events[0] as DatabaseUiEvent.ShowError).message)

            collectJob.cancel()
        }

    @Test
    fun `onTitleChange should update UI state`() =
        runTest {
            // Given
            createViewModelInCreateMode()
            val newTitle = "New Commitment Title"

            // When
            viewModel.onTitleChange(newTitle)

            val collectJob =
                launch {
                    viewModel.commitmentUiState.collect { }
                }
            advanceUntilIdle()

            // Then
            assertEquals(newTitle, viewModel.commitmentUiState.value.title)

            collectJob.cancel()
        }

    @Test
    fun `onDescriptionChange should update UI state`() =
        runTest {
            // Given
            createViewModelInCreateMode()
            val newDescription = "New Description"

            // When
            viewModel.onDescriptionChange(newDescription)

            val collectJob =
                launch {
                    viewModel.commitmentUiState.collect { }
                }
            advanceUntilIdle()

            // Then
            assertEquals(newDescription, viewModel.commitmentUiState.value.description)

            collectJob.cancel()
        }

    @Test
    fun `onStartInstantChange should update UI state`() =
        runTest {
            // Given
            createViewModelInCreateMode()
            val newStartInstant = testInstant + 2.hours

            // When
            viewModel.onStartInstantChange(newStartInstant)

            val collectJob =
                launch {
                    viewModel.commitmentUiState.collect { }
                }
            advanceUntilIdle()

            // Then
            assertEquals(newStartInstant, viewModel.commitmentUiState.value.startInstant)

            collectJob.cancel()
        }

    @Test
    fun `onEndInstantChange should update UI state`() =
        runTest {
            // Given
            createViewModelInCreateMode()
            val newEndInstant = testEndInstant + 2.hours

            // When
            viewModel.onEndInstantChange(newEndInstant)

            val collectJob =
                launch {
                    viewModel.commitmentUiState.collect { }
                }
            advanceUntilIdle()

            // Then
            assertEquals(newEndInstant, viewModel.commitmentUiState.value.endInstant)

            collectJob.cancel()
        }

    @Test
    fun `onPriorityChange should update UI state`() =
        runTest {
            // Given
            createViewModelInCreateMode()
            val newPriority = PriorityEnum.HIGH

            // When
            viewModel.onPriorityChange(newPriority)

            val collectJob =
                launch {
                    viewModel.commitmentUiState.collect { }
                }
            advanceUntilIdle()

            // Then
            assertEquals(newPriority, viewModel.commitmentUiState.value.priorityEnum)

            collectJob.cancel()
        }

    @Test
    fun `onFrequencyChange should update recurrence UI state`() =
        runTest {
            // Given
            createViewModelInCreateMode()
            val newFrequency = FrequencyEnum.WEEKLY

            // When
            viewModel.onFrequencyChange(newFrequency)

            val collectJob =
                launch {
                    viewModel.recurrenceUiState.collect { }
                }
            advanceUntilIdle()

            // Then
            assertEquals(newFrequency, viewModel.recurrenceUiState.value.frequencyEnum)

            collectJob.cancel()
        }

    @Test
    fun `onIntervalChange should update recurrence UI state`() =
        runTest {
            // Given
            createViewModelInCreateMode()
            val newInterval = 2

            // When
            viewModel.onIntervalChange(newInterval)

            val collectJob =
                launch {
                    viewModel.recurrenceUiState.collect { }
                }
            advanceUntilIdle()

            // Then
            assertEquals(newInterval, viewModel.recurrenceUiState.value.interval)

            collectJob.cancel()
        }

    @Test
    fun `onDaysOfWeekChange should update recurrence UI state`() =
        runTest {
            // Given
            createViewModelInCreateMode()
            val daysOfWeek = listOf(DayOfWeekEnum.MONDAY, DayOfWeekEnum.WEDNESDAY)

            // When
            viewModel.onDaysOfWeekChange(daysOfWeek)

            val collectJob =
                launch {
                    viewModel.recurrenceUiState.collect { }
                }
            advanceUntilIdle()

            // Then
            assertEquals(daysOfWeek, viewModel.recurrenceUiState.value.daysOfWeekList)

            collectJob.cancel()
        }

    @Test
    fun `onDayOfMonthChange should update recurrence UI state`() =
        runTest {
            // Given
            createViewModelInCreateMode()
            val dayOfMonth = 15

            // When
            viewModel.onDayOfMonthChange(dayOfMonth)

            val collectJob =
                launch {
                    viewModel.recurrenceUiState.collect { }
                }
            advanceUntilIdle()

            // Then
            assertEquals(dayOfMonth, viewModel.recurrenceUiState.value.dayOfMonth)

            collectJob.cancel()
        }

    @Test
    fun `onRecurrenceFormActiveChange should update recurrence UI state`() =
        runTest {
            // Given
            createViewModelInCreateMode()

            // When
            viewModel.onRecurrenceFormActiveChange(true)

            val collectJob =
                launch {
                    viewModel.recurrenceUiState.collect { }
                }
            advanceUntilIdle()

            // Then
            assertEquals(true, viewModel.recurrenceUiState.value.isRecurrenceActive)

            collectJob.cancel()
        }

    @Test
    fun `insertCommitment with empty title should emit ShowError event`() =
        runTest {
            // Given
            val events = mutableListOf<DatabaseUiEvent>()
            createViewModelInCreateMode()

            viewModel.onStartInstantChange(testInstant)
            viewModel.onEndInstantChange(testEndInstant)
            val collectJob =
                launch {
                    viewModel.commitmentUiState.collect { }
                }
            advanceUntilIdle()

            // When
            val eventCollectJob =
                launch {
                    viewModel.insertCommitment()
                    viewModel.events.collect { events.add(it) }
                }

            advanceUntilIdle()

            // Then
            assertEquals(1, events.size)
            assertEquals(1, events.size)
            assertEquals(strings.commitmentTitleEmptyError, (events[0] as DatabaseUiEvent.ShowError).message)

            collectJob.cancel()
            eventCollectJob.cancel()
        }

    @Test
    fun `insertCommitment with start time greater than end time should emit ShowError event`() =
        runTest {
            // Given
            val events = mutableListOf<DatabaseUiEvent>()
            createViewModelInCreateMode()

            // When
            val collectJob =
                launch {
                    viewModel.insertCommitment()
                    viewModel.events.collect { events.add(it) }
                }
            advanceUntilIdle()

            // Then
            assertEquals(1, events.size)
            assertEquals(
                strings.commitmentStartTimeError,
                (events[0] as DatabaseUiEvent.ShowError).message,
            )

            collectJob.cancel()
        }

    @Test
    fun `insertCommitment with scheduling conflicts should emit ShowError event`() =
        runTest {
            // Given
            val events = mutableListOf<DatabaseUiEvent>()

            createViewModelInCreateMode()
            advanceUntilIdle()

            viewModel.onTitleChange("Valid title")
            viewModel.onStartInstantChange(testInstant)
            viewModel.onEndInstantChange(testEndInstant)
            val uiStateCollectJob =
                launch {
                    viewModel.commitmentUiState.collect { }
                }
            advanceUntilIdle()

            val eventCollectJob =
                launch {
                    viewModel.events.collect { events.add(it) }
                }
            advanceUntilIdle()

            coEvery {
                commitmentRepository.checkSchedulingConflictsBetweenCommitments(
                    testInstant,
                    testEndInstant,
                    calendarId,
                )
            } returns 1

            // When
            viewModel.insertCommitment()
            advanceUntilIdle()

            // Then
            assertEquals(1, events.size)
            assertEquals(
                strings.commitmentConflictError,
                (events[0] as DatabaseUiEvent.ShowError).message,
            )

            uiStateCollectJob.cancel()
            eventCollectJob.cancel()
        }

    @Test
    fun `insertCommitment with valid data should call repository and emit Saved event`() =
        runTest {
            // Given
            val events = mutableListOf<DatabaseUiEvent>()
            createViewModelInCreateMode()

            viewModel.onTitleChange("Valid title")
            viewModel.onStartInstantChange(testInstant)
            viewModel.onEndInstantChange(testEndInstant)
            val uiStateCollectJob =
                launch {
                    viewModel.commitmentUiState.collect { }
                }
            advanceUntilIdle()

            val eventCollectJob =
                launch {
                    viewModel.events.collect { events.add(it) }
                }
            advanceUntilIdle()

            coEvery {
                commitmentRepository.checkSchedulingConflictsBetweenCommitments(
                    testInstant,
                    testInstant + 30.minutes,
                    calendarId,
                )
            } returns 0
            coEvery { commitmentRepository.insertCommitment(any()) } returns 1L

            // When
            viewModel.insertCommitment()
            advanceUntilIdle()

            // Then
            coVerify {
                commitmentRepository.checkSchedulingConflictsBetweenCommitments(
                    testInstant,
                    testInstant + 30.minutes,
                    calendarId,
                )
                commitmentRepository.insertCommitment(any())
            }
            assertEquals(1, events.size)
            assertEquals(DatabaseUiEvent.Saved, events[0])

            uiStateCollectJob.cancel()
            eventCollectJob.cancel()
        }

    @Test
    fun `insertCommitment with recurrence should insert both commitment and recurrence`() =
        runTest {
            // Given
            val events = mutableListOf<DatabaseUiEvent>()
            createViewModelInCreateMode()

            viewModel.onTitleChange("Test Commitment")
            viewModel.onStartInstantChange(testInstant)
            viewModel.onEndInstantChange(testEndInstant)
            viewModel.onRecurrenceFormActiveChange(true)
            viewModel.onFrequencyChange(FrequencyEnum.WEEKLY)
            viewModel.onIntervalChange(1)
            viewModel.onDaysOfWeekChange(listOf(DayOfWeekEnum.MONDAY))
            val uiStateCollectJob =
                launch {
                    viewModel.commitmentUiState.collect { }
                }
            advanceUntilIdle()

            val eventCollectJob =
                launch {
                    viewModel.events.collect { events.add(it) }
                }
            advanceUntilIdle()

            coEvery {
                commitmentRepository.checkSchedulingConflictsBetweenCommitments(
                    any(),
                    any(),
                    calendarId,
                )
            } returns 0
            coEvery { commitmentRepository.insertCommitment(any()) } returns 1L
            coEvery { recurrenceRepository.insert(any()) } returns Unit

            // When
            viewModel.insertCommitment()
            advanceUntilIdle()

            // Then
            coVerify { commitmentRepository.insertCommitment(any()) }
            coVerify { recurrenceRepository.insert(any()) }
            assertEquals(1, events.size)
            assertEquals(DatabaseUiEvent.Saved, events[0])

            uiStateCollectJob.cancel()
            eventCollectJob.cancel()
        }

    @Test
    fun `updateCommitment with empty title should emit ShowError event`() =
        runTest {
            // Given
            val events = mutableListOf<DatabaseUiEvent>()
            createViewModelInEditMode()
            advanceUntilIdle()

            viewModel.onTitleChange("")
            viewModel.onStartInstantChange(testInstant)
            viewModel.onEndInstantChange(testEndInstant)
            val uiStateCollectJob =
                launch {
                    viewModel.commitmentUiState.collect { }
                }
            advanceUntilIdle()

            val eventCollectJob =
                launch {
                    viewModel.events.collect { events.add(it) }
                }
            advanceUntilIdle()

            // When & Then
            viewModel.updateCommitment()
            advanceUntilIdle()

            assertEquals(1, events.size)
            assertEquals(strings.commitmentTitleEmptyError, (events[0] as DatabaseUiEvent.ShowError).message)

            uiStateCollectJob.cancel()
            eventCollectJob.cancel()
        }

    @Test
    fun `updateCommitment with start time greater than end time should emit ShowError event`() =
        runTest {
            // Given
            val events = mutableListOf<DatabaseUiEvent>()
            createViewModelInEditMode()
            advanceUntilIdle()

            viewModel.onTitleChange("Valid Title")
            viewModel.onStartInstantChange(testEndInstant)
            viewModel.onEndInstantChange(testInstant)
            val uiStateCollectJob =
                launch {
                    viewModel.commitmentUiState.collect { }
                }
            advanceUntilIdle()

            val eventCollectJob =
                launch {
                    viewModel.events.collect { events.add(it) }
                }
            advanceUntilIdle()

            // When & Then
            viewModel.updateCommitment()
            advanceUntilIdle()

            assertEquals(1, events.size)
            assertEquals(
                strings.commitmentStartTimeError,
                (events[0] as DatabaseUiEvent.ShowError).message,
            )

            uiStateCollectJob.cancel()
            eventCollectJob.cancel()
        }

    @Test
    fun `updateCommitment with scheduling conflicts should emit ShowError event`() =
        runTest {
            // Given
            val events = mutableListOf<DatabaseUiEvent>()
            val commitmentId = 1L
            createViewModelInEditMode(commitmentId)
            advanceUntilIdle()

            viewModel.onTitleChange("Updated Commitment")
            viewModel.onPriorityChange(PriorityEnum.HIGH)
            viewModel.onStartInstantChange(testInstant + 1.hours)
            viewModel.onEndInstantChange(testEndInstant + 1.hours)
            val uiStateCollectJob =
                launch {
                    viewModel.commitmentUiState.collect { }
                }
            advanceUntilIdle()

            val eventCollectJob =
                launch {
                    viewModel.events.collect { events.add(it) }
                }
            advanceUntilIdle()

            coEvery {
                commitmentRepository.checkSchedulingConflictsBetweenCommitments(
                    any(),
                    any(),
                    calendarId,
                    commitmentId,
                )
            } returns 1

            // When & Then
            viewModel.updateCommitment()
            advanceUntilIdle()

            assertEquals(1, events.size)
            assertEquals(
                strings.commitmentConflictError,
                (events[0] as DatabaseUiEvent.ShowError).message,
            )

            uiStateCollectJob.cancel()
            eventCollectJob.cancel()
        }

    @Test
    fun `updateCommitment with valid data should call repository and emit Saved event`() =
        runTest {
            // Given
            val events = mutableListOf<DatabaseUiEvent>()
            val commitmentId = 1L
            createViewModelInEditMode(commitmentId)
            advanceUntilIdle()

            viewModel.onTitleChange("Updated Commitment")
            viewModel.onPriorityChange(PriorityEnum.HIGH)
            val uiStateCollectJob =
                launch {
                    viewModel.commitmentUiState.collect { }
                }
            advanceUntilIdle()

            val eventCollectJob =
                launch {
                    viewModel.events.collect { events.add(it) }
                }
            advanceUntilIdle()

            coEvery {
                commitmentRepository.checkSchedulingConflictsBetweenCommitments(
                    any(),
                    any(),
                    calendarId,
                    commitmentId,
                )
            } returns 0
            coEvery { commitmentRepository.updateCommitment(any()) } returns Unit

            // When & Then
            viewModel.updateCommitment()
            advanceUntilIdle()

            coVerify { commitmentRepository.updateCommitment(any()) }
            assertEquals(1, events.size)
            assertEquals(DatabaseUiEvent.Saved, events[0])

            uiStateCollectJob.cancel()
            eventCollectJob.cancel()
        }

    @Test
    fun `updateCommitment with commitment not found should emit ShowError event`() =
        runTest {
            // Given
            val commitmentId = 1L
            val events = mutableListOf<DatabaseUiEvent>()
            coEvery { commitmentRepository.getCommitment(commitmentId) } returns null

            // When & Then
            val collectJob =
                launch {
                    val tempViewModel =
                        CommitmentFormViewModel(
                            CommitmentFormMode.Edit(commitmentId),
                            commitmentRepository,
                            settingsRepository,
                            recurrenceRepository,
                            taskNotificationScheduler,
                            strings,
                        )
                    tempViewModel.events.collect { events.add(it) }
                }

            advanceUntilIdle()

            assertEquals(1, events.size)
            assertEquals(strings.commitmentNotFoundError, (events[0] as DatabaseUiEvent.ShowError).message)

            collectJob.cancel()
        }

    @Test
    fun `updateCommitment with existing recurrence should update it`() =
        runTest {
            // Given
            val commitmentId = 1L
            val existingRecurrence =
                RecurrenceEntity(
                    id = 1L,
                    commitment = commitmentId,
                    frequency = FrequencyEnum.DAILY,
                    interval = 1,
                    dayOfWeekList = emptyList(),
                    dayOfMonth = 0,
                )
            createViewModelInEditMode(commitmentId)
            advanceUntilIdle()

            viewModel.onRecurrenceFormActiveChange(true)
            viewModel.onFrequencyChange(FrequencyEnum.CUSTOMIZED)
            viewModel.onIntervalChange(7)
            val uiStateCollectJob =
                launch {
                    viewModel.commitmentUiState.collect { }
                }
            advanceUntilIdle()

            coEvery {
                commitmentRepository.checkSchedulingConflictsBetweenCommitments(
                    any(),
                    any(),
                    calendarId,
                    commitmentId,
                )
            } returns 0
            coEvery { commitmentRepository.updateCommitment(any()) } returns Unit
            coEvery { recurrenceRepository.getRecurrenceByCommitment(commitmentId) } returns existingRecurrence
            coEvery { recurrenceRepository.update(any()) } returns Unit

            // When
            viewModel.updateCommitment()
            advanceUntilIdle()

            // Then
            coVerify { recurrenceRepository.update(any()) }

            uiStateCollectJob.cancel()
        }

    @Test
    fun `updateCommitment removing recurrence should delete it`() =
        runTest {
            // Given
            val commitmentId = 1L
            val existingRecurrence =
                RecurrenceEntity(
                    id = 1L,
                    commitment = commitmentId,
                    frequency = FrequencyEnum.DAILY,
                    interval = 1,
                    dayOfWeekList = emptyList(),
                    dayOfMonth = 0,
                )
            createViewModelInEditMode(commitmentId)
            advanceUntilIdle()

            viewModel.onRecurrenceFormActiveChange(false)
            val uiStateCollectJob =
                launch {
                    viewModel.commitmentUiState.collect { }
                }
            advanceUntilIdle()

            coEvery {
                commitmentRepository.checkSchedulingConflictsBetweenCommitments(
                    any(),
                    any(),
                    calendarId,
                    commitmentId,
                )
            } returns 0
            coEvery { commitmentRepository.updateCommitment(any()) } returns Unit
            coEvery { recurrenceRepository.getRecurrenceByCommitment(commitmentId) } returns existingRecurrence
            coEvery { recurrenceRepository.delete(any()) } returns Unit

            // When

            viewModel.updateCommitment()
            advanceUntilIdle()

            // Then
            coVerify { recurrenceRepository.delete(existingRecurrence) }

            uiStateCollectJob.cancel()
        }

    @Test
    fun `insertCommitment should not schedule notification when notification option is NO_SEND`() =
        runTest {
            // Given
            createViewModelInCreateMode()
            advanceUntilIdle()

            coEvery {
                commitmentRepository.checkSchedulingConflictsBetweenCommitments(
                    any(),
                    any(),
                    any(),
                )
            } returns 0
            coEvery { commitmentRepository.insertCommitment(any()) } returns 1L

            // When
            viewModel.insertCommitment()
            advanceUntilIdle()

            // Then
            coVerify(exactly = 0) { taskNotificationScheduler.scheduleTaskNotification(any()) }
        }

    @Test
    fun `insertCommitment with future time should schedule notification when option is set`() =
        runTest {
            // Given
            coEvery { settingsRepository.notificationOptionFlow } returns flowOf(NotificationEnum.ALL_COMMITMENT)
            createViewModelInCreateMode()

            viewModel.onTitleChange("Test Commitment")
            viewModel.onStartInstantChange(testInstant)
            viewModel.onEndInstantChange(testEndInstant)
            val uiStateCollectJob =
                launch {
                    viewModel.commitmentUiState.collect { }
                }
            advanceUntilIdle()

            coEvery {
                commitmentRepository.checkSchedulingConflictsBetweenCommitments(
                    any(),
                    any(),
                    any(),
                )
            } returns 0
            coEvery { commitmentRepository.insertCommitment(any()) } returns 1L

            // When
            viewModel.insertCommitment()
            advanceUntilIdle()

            // Then
            coVerify { taskNotificationScheduler.scheduleTaskNotification(any()) }

            uiStateCollectJob.cancel()
        }

    @Test
    fun `commitmentUiState should include notification option from settings`() =
        runTest {
            // Given
            coEvery { settingsRepository.notificationOptionFlow } returns flowOf(NotificationEnum.MEDIUM_AND_HIGH_PRIORITY)

            // When
            createViewModelInCreateMode()
            advanceUntilIdle()

            // Collect to trigger StateFlow subscription
            val collectJob =
                launch {
                    viewModel.commitmentUiState.collect { }
                }
            advanceUntilIdle()

            // Then
            assertEquals(NotificationEnum.MEDIUM_AND_HIGH_PRIORITY, viewModel.commitmentUiState.value.notificationOption)

            collectJob.cancel()
        }
}
