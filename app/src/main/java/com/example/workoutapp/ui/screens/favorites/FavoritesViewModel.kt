package com.example.workoutapp.ui.screens.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workoutapp.data.repository.ExerciseRepository
import com.example.workoutapp.domain.model.Exercise
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FavoritesUiState(
    val favorites: List<Exercise> = emptyList(),
    val isLoading: Boolean = false,
    val groupBy: GroupBy = GroupBy.NONE
)

enum class GroupBy {
    NONE, BODY_PART, EQUIPMENT, DIFFICULTY
}

class FavoritesViewModel(
    private val repository: ExerciseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            repository.getFavoriteExercises().collect { favorites ->
                _uiState.value = _uiState.value.copy(
                    favorites = favorites,
                    isLoading = false
                )
            }
        }
    }

    fun setGroupBy(groupBy: GroupBy) {
        _uiState.value = _uiState.value.copy(groupBy = groupBy)
    }

    fun removeFavorite(exerciseId: String) {
        viewModelScope.launch {
            repository.toggleFavorite(exerciseId, false)
        }
    }

    fun clearAllFavorites() {
        viewModelScope.launch {
            _uiState.value.favorites.forEach { exercise ->
                repository.toggleFavorite(exercise.id, false)
            }
        }
    }
}