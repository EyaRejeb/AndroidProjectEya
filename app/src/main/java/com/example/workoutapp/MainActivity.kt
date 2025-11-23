package com.example.workoutapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.workoutapp.ui.navigation.AppNavigation
import com.example.workoutapp.ui.theme.WorkoutAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as WorkoutApplication

        setContent {
            WorkoutAppTheme {
                val navController = rememberNavController()
                AppNavigation(
                    navController = navController,
                    viewModelFactory = app.viewModelFactory
                )
            }
        }
    }
}