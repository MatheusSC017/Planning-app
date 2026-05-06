package com.matheus.planningapp.navigation

import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json

sealed class NavigationEvent {
    object NavigateToHome : NavigationEvent()

    object NavigateToCalendar : NavigationEvent()

    object NavigateToSettings : NavigationEvent()

    object NavigateToRecurrence : NavigationEvent()

    data class NavigateToCommitmentCreate(
        val calendarId: Long,
        val datetimeInstant: Instant,
    ) : NavigationEvent()

    data class NavigateToCommitmentEdit(
        val commitmentId: Long,
    ) : NavigationEvent()

    object NavigateBack : NavigationEvent()

    data class NavigateToDeepLink(
        val deepLink: String,
    ) : NavigationEvent()
}

class NavEventManager {
    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
    val navigationEvents: SharedFlow<NavigationEvent> = _navigationEvents.asSharedFlow()

    suspend fun navigateToHome() {
        _navigationEvents.emit(NavigationEvent.NavigateToHome)
    }

    suspend fun navigateToCalendar() {
        _navigationEvents.emit(NavigationEvent.NavigateToCalendar)
    }

    suspend fun navigateToSettings() {
        _navigationEvents.emit(NavigationEvent.NavigateToSettings)
    }

    suspend fun navigateToRecurrence() {
        _navigationEvents.emit(NavigationEvent.NavigateToRecurrence)
    }

    suspend fun navigateToCommitmentCreate(
        calendarId: Long,
        datetimeInstant: Instant,
    ) {
        _navigationEvents.emit(
            NavigationEvent.NavigateToCommitmentCreate(
                calendarId = calendarId,
                datetimeInstant = datetimeInstant,
            ),
        )
    }

    suspend fun navigateToCommitmentEdit(commitmentId: Long) {
        _navigationEvents.emit(NavigationEvent.NavigateToCommitmentEdit(commitmentId))
    }

    suspend fun navigateBack() {
        _navigationEvents.emit(NavigationEvent.NavigateBack)
    }

    suspend fun navigateToDeepLink(deepLink: String) {
        _navigationEvents.emit(NavigationEvent.NavigateToDeepLink(deepLink))
    }
}

fun NavHostController.handleNavigationEvent(event: NavigationEvent) {
    when (event) {
        NavigationEvent.NavigateToHome -> {
            navigate(HomeScreen.route) {
                popUpTo(HomeScreen.route) { inclusive = true }
            }
        }
        NavigationEvent.NavigateToCalendar -> {
            navigate(CalendarScreen.route)
        }
        NavigationEvent.NavigateToSettings -> {
            navigate(SettingScreen.route)
        }
        NavigationEvent.NavigateToRecurrence -> {
            navigate(RecurrenceScreen.route)
        }
        is NavigationEvent.NavigateToCommitmentCreate -> {
            val payload =
                Json.encodeToString(
                    CreateCommitmentPayload(
                        calendarId = event.calendarId,
                        datetimeInstant = event.datetimeInstant,
                    ),
                )
            navigate("${CommitmentFormScreen().route.substringBefore("/{mode}".substring(0))}/create/$payload")
        }
        is NavigationEvent.NavigateToCommitmentEdit -> {
            navigate("commitmentForm/edit/${event.commitmentId}")
        }
        NavigationEvent.NavigateBack -> {
            popBackStack()
        }
        is NavigationEvent.NavigateToDeepLink -> {
            navigate(event.deepLink)
        }
    }
}
