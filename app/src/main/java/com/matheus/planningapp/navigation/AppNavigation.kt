package com.matheus.planningapp.navigation

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.matheus.planningapp.ui.screens.CalendarScreen
import com.matheus.planningapp.ui.screens.CalendarsMenuScreen
import com.matheus.planningapp.viewmodel.commitment.CommitmentFormMode
import com.matheus.planningapp.ui.screens.CommitmentScreen
import com.matheus.planningapp.ui.screens.components.NavigationDrawerSheet
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json


@Composable
fun AppNavigation () {
    val navHostController: NavHostController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            NavigationDrawerSheet(
                onNavigateToCalendarScreen = {
                    navHostController.navigate(Screens.HomeScreen.route)
                    scope.launch { drawerState.close() }
                },
                onNavigateToCalendarsMenuScreen = {
                    navHostController.navigate(Screens.CalendarScreen.route)
                    scope.launch { drawerState.close() }
                },
                onNavigateToSettingsScreen = {
                    scope.launch { drawerState.close() }
                }
            )
        },
    ) {
        NavHost(
            navController = navHostController,
            startDestination = Screens.HomeScreen.route
        ) {
            composable(Screens.HomeScreen.route) {
                CalendarScreen(
                    onNavigateToAddCommitment = { datetimeInstant, selectedCalendar ->
                        val payload = Json.encodeToString(
                            CreateCommitmentPayload(
                                calendarId = selectedCalendar,
                                datetimeInstant = datetimeInstant
                            )
                        )

                        navHostController.navigate(
                            "${Screens.CommitmentFormScreen.route}/create/$payload"
                        )
                    },
                    onNavigateToUpdateCommitment = { commitmentId ->
                        navHostController.navigate(
                            "${Screens.CommitmentFormScreen.route}/edit/$commitmentId"
                        )
                    },
                    onMenuClick = {
                        scope.launch { drawerState.open() }
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
            composable(Screens.CalendarScreen.route) {
                CalendarsMenuScreen(
                    onMenuClick = {
                        scope.launch { drawerState.open() }
                    }
                )
            }
        }
    }
}