package com.matheus.planningapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.matheus.planningapp.view.CalendarScreen
import com.matheus.planningapp.view.CalendarsMenuScreen
import com.matheus.planningapp.viewmodel.CommitmentFormMode
import com.matheus.planningapp.view.CommitmentScreen
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json


@Composable
fun AppNavigation () {
    val navHostController: NavHostController = rememberNavController()

    NavHost(
        navController = navHostController,
        startDestination = Screens.CalendarScreen.route
    ) {
        composable(Screens.CalendarScreen.route) {
            CalendarScreen(
                onNavigateToAddCommitment = { datetimeInstant, selectedCalendar ->
                    val payload = Json.encodeToString(CreateCommitmentPayload(
                        calendarId = selectedCalendar,
                        datetimeInstant = datetimeInstant
                    ))

                    navHostController.navigate(
                        "${Screens.CommitmentFormScreen.route}/create/$payload"
                    )
                },
                onNavigateToUpdateCommitment = { commitmentId ->
                    navHostController.navigate(
                        "${Screens.CommitmentFormScreen.route}/edit/$commitmentId"
                    )
                },
                onNavigateToCalendarsMenu = {
                    navHostController.navigate(
                        Screens.CalendarsMenuScreen.route
                    )
                }
            )
        }
        composable(
            route = "${Screens.CommitmentFormScreen.route}/{mode}/{payload}",
            arguments = listOf(
                navArgument("mode") { type = NavType.StringType },
                navArgument("payload") { type = NavType.StringType }
            )
        ) { backStackEntry ->

            val modeArg = backStackEntry.arguments?.getString("mode")!!
            val payloadArg = backStackEntry.arguments?.getString("payload")!!

            val mode = when (modeArg) {
                "create" -> {
                    val payloadData = Json.decodeFromString<CreateCommitmentPayload>(payloadArg)

                    CommitmentFormMode.Create(
                        calendarId = payloadData.calendarId,
                        initialInstant = payloadData.datetimeInstant
                    )
                }
                "edit" -> {
                    CommitmentFormMode.Edit(
                        commitmentId = payloadArg.toInt()
                    )
                }
                else -> error("Invalid mode")
            }

            CommitmentScreen(
                onBackPressed = {
                    navHostController.popBackStack()
                },
                commitmentFormMode = mode
            )

        }
        composable(Screens.CalendarsMenuScreen.route) {
            CalendarsMenuScreen(
                onNavigateToCalendarScreen = {
                    navHostController.navigate(
                        Screens.CalendarScreen.route
                    )
                }
            )
        }
    }
}