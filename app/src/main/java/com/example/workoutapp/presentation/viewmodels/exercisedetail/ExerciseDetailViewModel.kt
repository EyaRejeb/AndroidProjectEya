package com.example.workoutapp.presentation.viewmodels.exercisedetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workoutapp.data.preferences.PreferencesManager
import com.example.workoutapp.data.repository.ExerciseRepository
import com.example.workoutapp.domain.model.Exercise
import com.example.workoutapp.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ExerciseDetailUiState(
    val exercise: Exercise? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class ExerciseDetailViewModel(
    private val repository: ExerciseRepository,
    private val preferencesManager: PreferencesManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExerciseDetailUiState())
    val uiState: StateFlow<ExerciseDetailUiState> = _uiState.asStateFlow()

    private val exerciseId: String = savedStateHandle.get<String>("exerciseId") ?: ""

    init {
        loadExercise()
    }

    private fun loadExercise() {
        viewModelScope.launch {
            repository.getExerciseById(exerciseId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let { exercise ->
                            _uiState.value = _uiState.value.copy(
                                exercise = exercise,
                                isLoading = false,
                                error = null
                            )

                            // Incrémenter le compteur de vues d'exercices
                            viewModelScope.launch {
                                preferencesManager.incrementExercisesViewed()
                            }
                        }
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                }
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            _uiState.value.exercise?.let { exercise ->
                val newFavoriteStatus = !exercise.isFavorite
                repository.toggleFavorite(exercise.id, newFavoriteStatus)
                _uiState.value = _uiState.value.copy(
                    exercise = exercise.copy(isFavorite = newFavoriteStatus)
                )
            }
        }
    }

    fun retry() {
        loadExercise()
    }
}