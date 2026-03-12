package com.matheus.planningapp.navigation

sealed class Screens(val route: String) {
    object HomeScreen: Screens("home")
    object CommitmentFormScreen: Screens( "commitmentForm")
    object RecurrenceFormScreen: Screens("recurrenceForm")
    object CalendarScreen: Screens("calendar")
    object SettingScreen: Screens("setting")
}