package com.matheus.planningapp.navigation

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

sealed interface NavigationRoute {
    val route: String
    val deepLinkPattern: String
}

@Serializable
object HomeScreen : NavigationRoute {
    override val route = "home"
    override val deepLinkPattern = "planningapp://home"
}

@Serializable
data class CommitmentFormScreen(
    val mode: CommitmentFormMode = CommitmentFormMode.Create(),
) : NavigationRoute {
    override val route = "commitmentForm/{mode}/{payload}"
    override val deepLinkPattern = "planningapp://commitment/{mode}/{payload}"
}

@Serializable
object CalendarScreen : NavigationRoute {
    override val route = "calendar"
    override val deepLinkPattern = "planningapp://calendar"
}

@Serializable
object SettingScreen : NavigationRoute {
    override val route = "setting"
    override val deepLinkPattern = "planningapp://settings"
}

@Serializable
object RecurrenceScreen : NavigationRoute {
    override val route = "recurrence"
    override val deepLinkPattern = "planningapp://recurrence"
}

@Serializable
object CategoryScreen : NavigationRoute {
    override val route = "category"
    override val deepLinkPattern = "planningapp://category"
}

@Serializable
object FocusModeScreen : NavigationRoute {
    override val route = "focusMode"
    override val deepLinkPattern = "planningapp://focus-mode"
}

@Serializable
object FocusHistoryScreenRoute : NavigationRoute {
    override val route = "focusHistory"
    override val deepLinkPattern = "planningapp://focus-history"
}

@Serializable
sealed class CommitmentFormMode {
    @Serializable
    data class Create(
        val calendarId: Long = 0,
        val initialInstant: Instant? = null,
    ) : CommitmentFormMode()

    @Serializable
    data class Edit(
        val commitmentId: Long = 0,
    ) : CommitmentFormMode()
}
