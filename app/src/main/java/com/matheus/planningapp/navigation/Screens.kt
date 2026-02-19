package com.matheus.planningapp.navigation

sealed class Screens(val route: String) {
    object HomeScreen: Screens("home")
    object CommitmentFormScreen: Screens( "commitmentForm")
    object CalendarScreen: Screens("calendar")
}