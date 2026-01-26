package com.matheus.planningapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.matheus.planningapp.view.CalendarScreen
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
                onNavigateToCommitment = { date, selectedCalendar ->
                    navHostController.navigate(
                        "${Screens.CommitmentFormScreen.route}/$selectedCalendar/${Json.encodeToString(date)}"
                    )
                }
            )
        }
        composable(
            route = "${Screens.CommitmentFormScreen.route}/{selectedCalendar}/{date}",
            arguments = listOf(
                navArgument("date") {
                    type = NavType.StringType
                }
            )
        ) {
            val selectedCalendar = it.arguments?.getString("selectedCalendar")?.toInt()
            val date = it.arguments?.getString("date")
            requireNotNull(selectedCalendar)
            requireNotNull(date)

            val instant = Json.decodeFromString<Instant>(date)
            CommitmentScreen(
                onBackPressed = {
                    navHostController.popBackStack()
                },
                selectedCalendar = selectedCalendar,
                instant = instant
            )
        }
    }
}