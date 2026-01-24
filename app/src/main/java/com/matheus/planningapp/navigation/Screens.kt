package com.matheus.planningapp.navigation

sealed class Screens(val route: String) {
    object CalendarScreen: Screens("calendar")
    object CommitmentScreen: Screens("commitment")
//    {
//        fun createRoute(date: Long) = "commitment/$date"
//    }
}