package com.matheus.planningapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import com.matheus.planningapp.navigation.AppNavigation
import com.matheus.planningapp.ui.theme.PlanningAppTheme
import com.matheus.planningapp.ui.theme.strings.LocalStrings
import com.matheus.planningapp.ui.theme.strings.StringsRepository
import org.koin.android.ext.android.get

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val selectedLanguage: StringsRepository = get<StringsRepository>()

        setContent {
            PlanningAppTheme {
                CompositionLocalProvider(
                    LocalStrings provides selectedLanguage,
                ) {
                    AppNavigation()
                }
            }
        }
    }
}
