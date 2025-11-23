package com.example.workoutapp.ui.screens.exerciselist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workoutapp.data.repository.ExerciseRepository
import com.example.workoutapp.domain.model.Exercise
import com.example.workoutapp.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ExerciseListUiState(
    val exercises: List<Exercise> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val bodyPart: String = ""
)

class ExerciseListViewModel(
    private val repository: ExerciseRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExerciseListUiState())
    val uiState: StateFlow<ExerciseListUiState> = _uiState.asStateFlow()

    init {
        val bodyPart = savedStateHandle.get<String>("bodyPart") ?: ""
        _uiState.value = _uiState.value.copy(bodyPart = bodyPart)
        loadExercises(bodyPart)
    }

    private fun loadExercises(bodyPart: String) {
        viewModelScope.launch {
            repository.getExercisesByBodyPart(bodyPart).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            exercises = result.data ?: emptyList(),
                            isLoading = false,
                            error = null
                        )
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

    fun retry() {
        loadExercises(_uiState.value.bodyPart)
    }
}