package com.matheus.planningapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.matheus.planningapp.ui.theme.PlanningAppTheme
import com.matheus.planningapp.view.CalendarScreen
import com.matheus.planningapp.view.PlanningTopAppBar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlanningAppTheme {
                Scaffold(
                    topBar = {
                        PlanningTopAppBar(
                            modifier = Modifier
                                .padding(8.dp)
                        )
                    },
                    content = { paddingValues ->
                        CalendarScreen(
                            modifier = Modifier
                                .padding(paddingValues)
                        )
                    }
                )
            }
        }
    }
}
