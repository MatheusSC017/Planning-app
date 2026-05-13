package com.matheus.planningapp.navigation

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.matheus.planningapp.ui.screens.CalendarScreen
import com.matheus.planningapp.ui.screens.SettingScreen
import com.matheus.planningapp.ui.screens.category.CategoryManagementScreen
import com.matheus.planningapp.ui.screens.commitment.CommitmentScreen
import com.matheus.planningapp.ui.screens.components.NavigationDrawerSheet
import com.matheus.planningapp.ui.screens.home.HomeScreen
import com.matheus.planningapp.ui.screens.recurrence.RecurrenceScreen
import com.matheus.planningapp.viewmodel.commitment.CommitmentFormMode
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

@Composable
fun AppNavigation(navEventManager: NavEventManager? = null) {
    val navHostController: NavHostController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    navEventManager?.let {
        LaunchedEffect(navEventManager) {
            navEventManager.navigationEvents.collect { event ->
                navHostController.handleNavigationEvent(event)
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            NavigationDrawerSheet(
                onNavigateToHomeScreen = {
                    scope.launch {
                        navEventManager?.navigateToHome()
                            ?: navHostController.navigate(HomeScreen.route)
                    }
                    scope.launch { drawerState.close() }
                },
                onNavigateToCalendarScreen = {
                    scope.launch {
                        navEventManager?.navigateToCalendar()
                            ?: navHostController.navigate(CalendarScreen.route)
                    }
                    scope.launch { drawerState.close() }
                },
                onNavigateToSettingsScreen = {
                    scope.launch {
                        navEventManager?.navigateToSettings()
                            ?: navHostController.navigate(SettingScreen.route)
                    }
                    scope.launch { drawerState.close() }
                },
                onNavigateToRecurrenceScreen = {
                    scope.launch {
                        navEventManager?.navigateToRecurrence()
                            ?: navHostController.navigate(RecurrenceScreen.route)
                    }
                    scope.launch { drawerState.close() }
                },
                onNavigateToCategoryScreen = {
                    scope.launch {
                        navHostController.navigate(CategoryScreen.route)
                    }
                    scope.launch { drawerState.close() }
                },
            )
        },
    ) {
        NavHost(
            navController = navHostController,
            startDestination = HomeScreen.route,
        ) {
            composable(
                route = HomeScreen.route,
                deepLinks =
                    listOf(
                        navDeepLink { uriPattern = HomeScreen.deepLinkPattern },
                    ),
            ) {
                HomeScreen(
                    onNavigateToAddCommitment = { datetimeInstant, selectedCalendar ->
                        scope.launch {
                            navEventManager?.navigateToCommitmentCreate(
                                calendarId = selectedCalendar,
                                datetimeInstant = datetimeInstant,
                            ) ?: run {
                                val payload =
                                    Json.encodeToString(
                                        CreateCommitmentPayload(
                                            calendarId = selectedCalendar,
                                            datetimeInstant = datetimeInstant,
                                        ),
                                    )
                                navHostController.navigate("commitmentForm/create/$payload")
                            }
                        }
                    },
                    onNavigateToUpdateCommitment = { commitmentId ->
                        scope.launch {
                            navEventManager?.navigateToCommitmentEdit(commitmentId)
                                ?: navHostController.navigate("commitmentForm/edit/$commitmentId")
                        }
                    },
                    onMenuClick = {
                        scope.launch { drawerState.open() }
                    },
                )
            }
            composable(
                route = CommitmentFormScreen().route,
                arguments =
                    listOf(
                        navArgument("mode") { type = NavType.StringType },
                        navArgument("payload") { type = NavType.StringType },
                    ),
                deepLinks =
                    listOf(
                        navDeepLink { uriPattern = CommitmentFormScreen().deepLinkPattern },
                    ),
            ) { backStackEntry ->
                val modeArg = backStackEntry.arguments?.getString("mode")!!
                val payloadArg = backStackEntry.arguments?.getString("payload")!!

                val mode =
                    when (modeArg) {
                        "create" -> {
                            val payloadData = Json.decodeFromString<CreateCommitmentPayload>(payloadArg)
                            CommitmentFormMode.Create(
                                calendarId = payloadData.calendarId,
                                initialInstant = payloadData.datetimeInstant,
                            )
                        }
                        "edit" -> {
                            CommitmentFormMode.Edit(
                                commitmentId = payloadArg.toLong(),
                            )
                        }
                        else -> error("Invalid mode: $modeArg")
                    }

                CommitmentScreen(
                    onBackPressed = {
                        scope.launch {
                            navEventManager?.navigateBack()
                                ?: navHostController.popBackStack()
                        }
                    },
                    commitmentFormMode = mode,
                )
            }

            composable(
                route = CalendarScreen.route,
                deepLinks =
                    listOf(
                        navDeepLink { uriPattern = CalendarScreen.deepLinkPattern },
                    ),
            ) {
                CalendarScreen(
                    onMenuClick = {
                        scope.launch { drawerState.open() }
                    },
                )
            }

            composable(
                route = RecurrenceScreen.route,
                deepLinks =
                    listOf(
                        navDeepLink { uriPattern = RecurrenceScreen.deepLinkPattern },
                    ),
            ) {
                RecurrenceScreen(
                    onMenuClick = {
                        scope.launch { drawerState.open() }
                    },
                    onNavigateToUpdateCommitment = { commitmentId ->
                        scope.launch {
                            navEventManager?.navigateToCommitmentEdit(commitmentId)
                                ?: navHostController.navigate("commitmentForm/edit/$commitmentId")
                        }
                    },
                )
            }

            composable(
                route = SettingScreen.route,
                deepLinks =
                    listOf(
                        navDeepLink { uriPattern = SettingScreen.deepLinkPattern },
                    ),
            ) {
                SettingScreen(
                    onMenuClick = {
                        scope.launch { drawerState.open() }
                    },
                )
            }

            composable(
                route = CategoryScreen.route,
                deepLinks =
                    listOf(
                        navDeepLink { uriPattern = CategoryScreen.deepLinkPattern },
                    ),
            ) {
                CategoryManagementScreen(
                    onMenuClick = {
                        scope.launch { drawerState.open() }
                    },
                )
            }
        }
    }
}
