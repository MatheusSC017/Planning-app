package com.matheus.planningapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import com.matheus.planningapp.navigation.AppNavigation
import com.matheus.planningapp.ui.theme.LocalStrings
import com.matheus.planningapp.ui.theme.PlanningAppTheme
import com.matheus.planningapp.ui.theme.StringsRepository
import com.matheus.planningapp.ui.theme.StringsRepositoryEnglish
import com.matheus.planningapp.ui.theme.StringsRepositoryPortuguese
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val selectedLanguage: StringsRepository = when (Locale.getDefault().language) {
            "pt" -> StringsRepositoryPortuguese()
            else -> StringsRepositoryEnglish()
        }
        Log.d("TAG", "onCreate: ${Locale.getDefault().language}")

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
