package com.matheus.planningapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import com.matheus.planningapp.navigation.AppNavigation
import com.matheus.planningapp.ui.theme.strings.LocalStrings
import com.matheus.planningapp.ui.theme.PlanningAppTheme
import com.matheus.planningapp.ui.theme.strings.StringsRepository
import com.matheus.planningapp.ui.theme.strings.StringsRepositoryEnglish
import com.matheus.planningapp.ui.theme.strings.StringsRepositoryPortuguese
import com.matheus.planningapp.ui.theme.strings.StringsRepositorySpanish
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val selectedLanguage: StringsRepository = when (Locale.getDefault().language) {
            "pt" -> StringsRepositoryPortuguese()
            "es" -> StringsRepositorySpanish()
            else -> StringsRepositoryEnglish()
        }

        setContent {
            PlanningAppTheme {
                CompositionLocalProvider(
                    LocalStrings provides selectedLanguage
                ) {
                    AppNavigation()
                }
            }
        }
    }
}
