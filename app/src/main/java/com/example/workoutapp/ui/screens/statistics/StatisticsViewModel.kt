package com.example.workoutapp.ui.screens.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workoutapp.data.repository.ExerciseRepository
import com.example.workoutapp.data.preferences.PreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class StatisticsUiState(
    val totalFavorites: Int = 0,
    val exercisesViewed: Int = 0,
    val appLaunches: Int = 0,
    val isLoading: Boolean = false
)

class StatisticsViewModel(
    private val repository: ExerciseRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    init {
        loadStatistics()
    }

    private fun loadStatistics() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // Charger les favoris
            repository.getFavoriteExercises().collect { favorites ->
                _uiState.value = _uiState.value.copy(
                    totalFavorites = favorites.size,
                    isLoading = false
                )
            }
        }

        viewModelScope.launch {
            preferencesManager.exercisesViewedCountFlow.collect { count ->
                _uiState.value = _uiState.value.copy(exercisesViewed = count)
            }
        }

        viewModelScope.launch {
            preferencesManager.appLaunchesCountFlow.collect { count ->
                _uiState.value = _uiState.value.copy(appLaunches = count)
            }
        }
    }
}