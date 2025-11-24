package com.example.workoutapp.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "workout_preferences")

class PreferencesManager(private val context: Context) {

    companion object {
        private val THEME_MODE = stringPreferencesKey("theme_mode") // "light", "dark", "system"
        private val FIRST_LAUNCH = booleanPreferencesKey("first_launch")
        private val EXERCISES_VIEWED_COUNT = intPreferencesKey("exercises_viewed_count")
        private val APP_LAUNCHES_COUNT = intPreferencesKey("app_launches_count")
    }

    // Theme Mode
    val themeModeFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[THEME_MODE] ?: "system"
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE] = mode
        }
    }

    // First Launch
    val isFirstLaunchFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[FIRST_LAUNCH] ?: true
    }

    suspend fun setFirstLaunchComplete() {
        context.dataStore.edit { preferences ->
            preferences[FIRST_LAUNCH] = false
        }
    }

    // Statistics - Exercises Viewed
    val exercisesViewedCountFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[EXERCISES_VIEWED_COUNT] ?: 0
    }

    suspend fun incrementExercisesViewed() {
        context.dataStore.edit { preferences ->
            val currentCount = preferences[EXERCISES_VIEWED_COUNT] ?: 0
            preferences[EXERCISES_VIEWED_COUNT] = currentCount + 1
        }
    }

    // Statistics - App Launches
    val appLaunchesCountFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[APP_LAUNCHES_COUNT] ?: 0
    }

    suspend fun incrementAppLaunches() {
        context.dataStore.edit { preferences ->
            val currentCount = preferences[APP_LAUNCHES_COUNT] ?: 0
            preferences[APP_LAUNCHES_COUNT] = currentCount + 1
        }
    }

    // Reset only statistics (keep theme and other settings)
    suspend fun resetStatistics() {
        context.dataStore.edit { preferences ->
            preferences[EXERCISES_VIEWED_COUNT] = 0
            preferences[APP_LAUNCHES_COUNT] = 0
        }
    }

    // Clear all data
    suspend fun clearAllData() {
        context.dataStore.edit { it.clear() }
    }
}