package com.example.workoutapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.workoutapp.ui.navigation.AppNavigation
import com.example.workoutapp.ui.theme.WorkoutAppTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Installer le splash screen natif
        installSplashScreen()

        super.onCreate(savedInstanceState)

        val app = application as WorkoutApplication

        // Incrémenter le compteur de lancements
        lifecycleScope.launch {
            app.preferencesManager.incrementAppLaunches()
        }

        // Charger les préférences de thème
        val initialThemeMode = runBlocking {
            app.preferencesManager.themeModeFlow.first()
        }

        setContent {
            val themeMode by app.preferencesManager.themeModeFlow.collectAsState(initial = initialThemeMode)
            val systemInDarkTheme = isSystemInDarkTheme()

            val darkTheme = when (themeMode) {
                "light" -> false
                "dark" -> true
                else -> systemInDarkTheme
            }

            WorkoutAppTheme(darkTheme = darkTheme) {
                val navController = rememberNavController()
                AppNavigation(
                    navController = navController,
                    viewModelFactory = app.viewModelFactory
                )
            }
        }
    }
}