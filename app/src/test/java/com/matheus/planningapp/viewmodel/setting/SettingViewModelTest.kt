package com.matheus.planningapp.viewmodel.setting

import android.content.Context
import androidx.activity.result.ActivityResultLauncher
import com.matheus.planningapp.data.commitment.CommitmentEntity
import com.matheus.planningapp.data.commitment.CommitmentRepository
import com.matheus.planningapp.datastore.SettingsRepository
import com.matheus.planningapp.util.enums.NotificationEnum
import com.matheus.planningapp.util.enums.PriorityEnum
import com.matheus.planningapp.util.enums.ViewEnum
import com.matheus.planningapp.util.notification.TaskNotificationScheduler
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
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

@OptIn(ExperimentalCoroutinesApi::class)
class SettingViewModelTest {
    private lateinit var context: Context
    private lateinit var commitmentRepository: CommitmentRepository
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var taskNotificationScheduler: TaskNotificationScheduler
    private lateinit var viewModel: SettingViewModel
    private lateinit var notificationPermissionLauncher: ActivityResultLauncher<String>
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)

        context = mockk<Context>()
        commitmentRepository = mockk<CommitmentRepository>()
        settingsRepository = mockk<SettingsRepository>()
        taskNotificationScheduler = mockk<TaskNotificationScheduler>()
        notificationPermissionLauncher = mockk<ActivityResultLauncher<String>>()

        every { notificationPermissionLauncher.launch(any()) } just Runs
        coEvery { settingsRepository.viewModeFlow } returns flowOf(ViewEnum.COLUMN)
        coEvery { settingsRepository.notificationOptionFlow } returns flowOf(NotificationEnum.NO_SEND)

        viewModel = SettingViewModel(context, commitmentRepository, settingsRepository, taskNotificationScheduler)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState should emit initial state with default settings from repositories`() =
        runTest {
            // Given
            val collectedStates = mutableListOf<SettingUiState>()

            // When
            val collectJob =
                launch {
                    viewModel.uiState.collect { state ->
                        collectedStates.add(state)
                    }
                }
            advanceUntilIdle()

            // Then
            assertTrue(collectedStates.isNotEmpty())
            assertEquals(ViewEnum.COLUMN, collectedStates.last().viewMode)
            assertEquals(NotificationEnum.NO_SEND, collectedStates.last().notificationOption)

            collectJob.cancel()
        }

    @Test
    fun `uiState should emit updated state when settings flow changes`() =
        runTest {
            // Given
            val collectedStates = mutableListOf<SettingUiState>()
            coEvery { settingsRepository.viewModeFlow } returns flowOf(ViewEnum.GRID)
            coEvery { settingsRepository.notificationOptionFlow } returns flowOf(NotificationEnum.ALL_COMMITMENT)

            val newViewModel = SettingViewModel(context, commitmentRepository, settingsRepository, taskNotificationScheduler)

            // When
            val collectJob =
                launch {
                    newViewModel.uiState.collect { state ->
                        collectedStates.add(state)
                    }
                }
            advanceUntilIdle()

            // Then
            assertTrue(collectedStates.isNotEmpty())
            assertEquals(ViewEnum.GRID, collectedStates.last().viewMode)
            assertEquals(NotificationEnum.ALL_COMMITMENT, collectedStates.last().notificationOption)

            collectJob.cancel()
        }

    @Test
    fun `updateSettings should save settings to repository when only view mode changes`() =
        runTest {
            // Given
            val newSettings = SettingUiState(viewMode = ViewEnum.GRID, notificationOption = NotificationEnum.NO_SEND)
            coEvery { settingsRepository.saveSettings(newSettings) } just Runs

            // When
            viewModel.updateSettings(newSettings, notificationPermissionLauncher)
            advanceUntilIdle()

            // Then
            coVerify { settingsRepository.saveSettings(newSettings) }
        }

    @Test
    fun `updateSettings should save settings when notification option changes`() =
        runTest {
            // Given
            val newSettings = SettingUiState(viewMode = ViewEnum.COLUMN, notificationOption = NotificationEnum.ALL_COMMITMENT)

            coEvery { commitmentRepository.getFutureCommitments() } returns emptyList()
            coEvery { settingsRepository.saveSettings(newSettings) } just Runs

            // When
            viewModel.updateSettings(newSettings, notificationPermissionLauncher)
            advanceUntilIdle()

            // Then - just verify that saveSettings was called
            coVerify(atLeast = 1) { settingsRepository.saveSettings(any()) }
        }

    @Test
    fun `setNotificationToFutureCommitment should schedule notifications for all future commitments`() =
        runTest {
            // Given
            val futureCommitments =
                listOf(
                    CommitmentEntity(
                        id = 1,
                        calendar = 1,
                        title = "Future Task 1",
                        description = null,
                        startDateTime = Clock.System.now(),
                        endDateTime = Clock.System.now().plus(kotlin.time.Duration.parse("1h")),
                        priorityEnum = PriorityEnum.HIGH,
                    ),
                    CommitmentEntity(
                        id = 2,
                        calendar = 1,
                        title = "Future Task 2",
                        description = null,
                        startDateTime = Clock.System.now().plus(kotlin.time.Duration.parse("1d")),
                        endDateTime = Clock.System.now().plus(kotlin.time.Duration.parse("1d1h")),
                        priorityEnum = PriorityEnum.MEDIUM,
                    ),
                )

            coEvery { commitmentRepository.getFutureCommitments() } returns futureCommitments
            coEvery { taskNotificationScheduler.scheduleTaskNotification(any()) } just Runs

            // When
            viewModel.setNotificationToFutureCommitment()
            advanceUntilIdle()

            // Then
            coVerify { commitmentRepository.getFutureCommitments() }
            coVerify(exactly = 2) { taskNotificationScheduler.scheduleTaskNotification(any()) }
        }

    @Test
    fun `setNotificationToFutureCommitment should handle empty commitment list`() =
        runTest {
            // Given
            coEvery { commitmentRepository.getFutureCommitments() } returns emptyList()
            coEvery { taskNotificationScheduler.scheduleTaskNotification(any()) } just Runs

            // When
            viewModel.setNotificationToFutureCommitment()
            advanceUntilIdle()

            // Then
            coVerify { commitmentRepository.getFutureCommitments() }
            coVerify(exactly = 0) { taskNotificationScheduler.scheduleTaskNotification(any()) }
        }

    @Test
    fun `deleteNotificationToFutureCommitments should cancel notifications for all future commitments`() =
        runTest {
            // Given
            val futureCommitments =
                listOf(
                    CommitmentEntity(
                        id = 1,
                        calendar = 1,
                        title = "Future Task 1",
                        description = null,
                        startDateTime = Clock.System.now(),
                        endDateTime = Clock.System.now().plus(kotlin.time.Duration.parse("1h")),
                        priorityEnum = PriorityEnum.HIGH,
                    ),
                    CommitmentEntity(
                        id = 2,
                        calendar = 1,
                        title = "Future Task 2",
                        description = null,
                        startDateTime = Clock.System.now().plus(kotlin.time.Duration.parse("1d")),
                        endDateTime = Clock.System.now().plus(kotlin.time.Duration.parse("1d1h")),
                        priorityEnum = PriorityEnum.MEDIUM,
                    ),
                )

            coEvery { commitmentRepository.getFutureCommitments() } returns futureCommitments
            coEvery { taskNotificationScheduler.cancelTaskNotification(any()) } just Runs

            // When
            viewModel.deleteNotificationToFutureCommitments()
            advanceUntilIdle()

            // Then
            coVerify { commitmentRepository.getFutureCommitments() }
            coVerify(exactly = 2) { taskNotificationScheduler.cancelTaskNotification(any()) }
        }

    @Test
    fun `deleteNotificationToFutureCommitments should handle empty commitment list`() =
        runTest {
            // Given
            coEvery { commitmentRepository.getFutureCommitments() } returns emptyList()
            coEvery { taskNotificationScheduler.cancelTaskNotification(any()) } just Runs

            // When
            viewModel.deleteNotificationToFutureCommitments()
            advanceUntilIdle()

            // Then
            coVerify { commitmentRepository.getFutureCommitments() }
            coVerify(exactly = 0) { taskNotificationScheduler.cancelTaskNotification(any()) }
        }

    @Test
    fun `updateSettings should preserve current view mode when only notification changes`() =
        runTest {
            // Given
            coEvery { settingsRepository.viewModeFlow } returns flowOf(ViewEnum.GRID)
            coEvery { settingsRepository.notificationOptionFlow } returns flowOf(NotificationEnum.NO_SEND)

            val newViewModel = SettingViewModel(context, commitmentRepository, settingsRepository, taskNotificationScheduler)

            val collectedStates = mutableListOf<SettingUiState>()

            val collectJob =
                launch {
                    newViewModel.uiState.collect { state ->
                        collectedStates.add(state)
                    }
                }
            advanceUntilIdle()

            val newSettings = SettingUiState(viewMode = ViewEnum.GRID, notificationOption = NotificationEnum.ALL_COMMITMENT)

            coEvery { commitmentRepository.getFutureCommitments() } returns emptyList()
            coEvery { taskNotificationScheduler.scheduleTaskNotification(any()) } just Runs
            coEvery { settingsRepository.saveSettings(newSettings) } just Runs

            // When
            newViewModel.updateSettings(newSettings, notificationPermissionLauncher)
            advanceUntilIdle()

            // Then
            assertEquals(ViewEnum.GRID, collectedStates.last().viewMode)

            collectJob.cancel()
        }

    @Test
    fun `updateSettings should handle switching between different notification options`() =
        runTest {
            // Given
            coEvery { settingsRepository.viewModeFlow } returns flowOf(ViewEnum.COLUMN)
            coEvery { settingsRepository.notificationOptionFlow } returns flowOf(NotificationEnum.ALL_COMMITMENT)

            val newViewModel = SettingViewModel(context, commitmentRepository, settingsRepository, taskNotificationScheduler)

            val collectJob =
                launch {
                    newViewModel.uiState.collect { }
                }
            advanceUntilIdle()

            val futureCommitments =
                listOf(
                    CommitmentEntity(
                        id = 1,
                        calendar = 1,
                        title = "Future Task 1",
                        description = null,
                        startDateTime = Clock.System.now(),
                        endDateTime = Clock.System.now().plus(kotlin.time.Duration.parse("1h")),
                        priorityEnum = PriorityEnum.HIGH,
                    ),
                )

            val newSettings = SettingUiState(viewMode = ViewEnum.COLUMN, notificationOption = NotificationEnum.ONLY_HIGH_PRIORITY)

            coEvery { settingsRepository.saveSettings(newSettings) } just Runs
            coEvery { commitmentRepository.getFutureCommitments() } returns futureCommitments
            coEvery { taskNotificationScheduler.cancelTaskNotification(any()) } just Runs
            coEvery { taskNotificationScheduler.scheduleTaskNotification(any()) } just Runs

            // When
            newViewModel.updateSettings(newSettings, notificationPermissionLauncher)
            advanceUntilIdle()

            // Then

            coVerify { commitmentRepository.getFutureCommitments() }
//        coVerify(atLeast = 1) { taskNotificationScheduler.cancelTaskNotification(any()) }
            collectJob.cancel()
        }

    @Test
    fun `updateSettings should not process notifications if only view mode changes`() =
        runTest {
            // Given
            coEvery { settingsRepository.viewModeFlow } returns flowOf(ViewEnum.COLUMN)
            coEvery { settingsRepository.notificationOptionFlow } returns flowOf(NotificationEnum.NO_SEND)

            val newViewModel = SettingViewModel(context, commitmentRepository, settingsRepository, taskNotificationScheduler)

            val newSettings = SettingUiState(viewMode = ViewEnum.GRID, notificationOption = NotificationEnum.NO_SEND)
            coEvery { settingsRepository.saveSettings(newSettings) } just Runs

            // When
            newViewModel.updateSettings(newSettings, notificationPermissionLauncher)
            advanceUntilIdle()

            // Then
            coVerify { settingsRepository.saveSettings(newSettings) }
            coVerify(exactly = 0) { commitmentRepository.getFutureCommitments() }
        }
}
