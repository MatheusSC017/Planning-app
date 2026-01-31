package com.matheus.planningapp.navigation

sealed class Screens(val route: String) {
    object CalendarScreen: Screens("calendar")
    object CommitmentFormScreen: Screens("commitmentForm")
    object CalendarsMenuScreen: Screens("calendarMenu")
}