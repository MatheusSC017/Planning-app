package com.matheus.planningapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.matheus.planningapp.navigation.AppNavigation
import com.matheus.planningapp.ui.theme.PlanningAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlanningAppTheme {
                AppNavigation()
            }
        }
    }
}
