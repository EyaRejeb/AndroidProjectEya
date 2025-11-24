package com.example.workoutapp.presentation.viewmodels.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workoutapp.data.local.database.WorkoutDatabase
import com.example.workoutapp.data.preferences.PreferencesManager
import com.example.workoutapp.data.repository.ExerciseRepository
import com.example.workoutapp.domain.model.Exercise
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val themeMode: String = "system",
    val favoritesCount: Int = 0,
    val exercisesViewed: Int = 0,
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

        viewModelScope.launch {
            preferencesManager.exercisesViewedCountFlow.collect { count ->
                _uiState.value = _uiState.value.copy(exercisesViewed = count)
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
            val favorites = mutableListOf<Exercise>()
            repository.getFavoriteExercises().collect { list ->
                favorites.addAll(list)
            }

            favorites.forEach { exercise ->
                repository.toggleFavorite(exercise.id, false)
            }
        }
    }

    fun clearStatistics() {
        viewModelScope.launch {
            // Réinitialiser uniquement les compteurs de statistiques
            // Ne pas toucher au thème et autres préférences
            preferencesManager.resetStatistics()
        }
    }
}