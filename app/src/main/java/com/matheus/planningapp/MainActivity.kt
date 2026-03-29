package com.matheus.planningapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import com.matheus.planningapp.navigation.AppNavigation
import com.matheus.planningapp.ui.theme.LocalStrings
import com.matheus.planningapp.ui.theme.PlanningAppTheme
import com.matheus.planningapp.ui.theme.StringsRepositoryEnglish

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlanningAppTheme {
                CompositionLocalProvider(
                    LocalStrings provides StringsRepositoryEnglish()
                ) {
                    AppNavigation()
                }
            }
        }
    }
}
