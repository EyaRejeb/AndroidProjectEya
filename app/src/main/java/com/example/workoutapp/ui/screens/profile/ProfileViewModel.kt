package com.example.workoutapp.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workoutapp.data.local.database.WorkoutDatabase
import com.example.workoutapp.data.preferences.PreferencesManager
import com.example.workoutapp.data.repository.ExerciseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val themeMode: String = "system",
    val favoritesCount: Int = 0,
    val isLoading: Boolean = false
)

class ProfileViewModel(
    private val repository: ExerciseRepository,
    private val preferencesManager: PreferencesManager,
    private val database: WorkoutDatabase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            preferencesManager.themeModeFlow.collect { mode ->
                _uiState.value = _uiState.value.copy(themeMode = mode)
            }
        }

        viewModelScope.launch {
            repository.getFavoriteExercises().collect { favorites ->
                _uiState.value = _uiState.value.copy(favoritesCount = favorites.size)
            }
        }
    }

    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            preferencesManager.setThemeMode(mode)
        }
    }

    fun clearCache() {
        viewModelScope.launch {
            database.exerciseDao().deleteAllExercises()
        }
    }

    fun clearAllFavorites() {
        viewModelScope.launch {
            repository.getFavoriteExercises().collect { favorites ->
                favorites.forEach { exercise ->
                    repository.toggleFavorite(exercise.id, false)
                }
            }
        }
    }

    fun clearStatistics() {
        viewModelScope.launch {
            // Réinitialiser les compteurs dans les préférences
            preferencesManager.clearAllData()
        }
    }
}