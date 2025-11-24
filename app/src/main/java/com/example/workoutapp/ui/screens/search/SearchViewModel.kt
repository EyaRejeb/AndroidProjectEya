package com.example.workoutapp.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workoutapp.data.remote.api.ExerciseApi
import com.example.workoutapp.data.repository.toEntity
import com.example.workoutapp.data.repository.toDomain
import com.example.workoutapp.domain.model.Exercise
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SearchUiState(
    val searchQuery: String = "",
    val exercises: List<Exercise> = emptyList(),
    val filteredExercises: List<Exercise> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,

    // Filtres
    val selectedBodyParts: Set<String> = emptySet(),
    val selectedEquipment: Set<String> = emptySet(),
    val selectedDifficulty: String? = null,

    // Listes disponibles
    val availableBodyParts: List<String> = emptyList(),
    val availableEquipment: List<String> = emptyList()
)

class SearchViewModel(
    private val api: ExerciseApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                // Charger les listes de filtres
                val bodyParts = api.getBodyPartList()
                val equipment = api.getEquipmentList()

                // Charger quelques exercices pour commencer
                val exercises = api.getExercises(limit = 50)
                    .map { it.toEntity().toDomain() }

                _uiState.value = _uiState.value.copy(
                    exercises = exercises,
                    filteredExercises = exercises,
                    availableBodyParts = bodyParts,
                    availableEquipment = equipment,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Erreur de chargement: ${e.message}"
                )
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        applyFilters()
    }

    fun toggleBodyPartFilter(bodyPart: String) {
        val current = _uiState.value.selectedBodyParts
        _uiState.value = _uiState.value.copy(
            selectedBodyParts = if (bodyPart in current) {
                current - bodyPart
            } else {
                current + bodyPart
            }
        )
        applyFilters()
    }

    fun toggleEquipmentFilter(equipment: String) {
        val current = _uiState.value.selectedEquipment
        _uiState.value = _uiState.value.copy(
            selectedEquipment = if (equipment in current) {
                current - equipment
            } else {
                current + equipment
            }
        )
        applyFilters()
    }

    fun setDifficultyFilter(difficulty: String?) {
        _uiState.value = _uiState.value.copy(selectedDifficulty = difficulty)
        applyFilters()
    }

    fun clearFilters() {
        _uiState.value = _uiState.value.copy(
            searchQuery = "",
            selectedBodyParts = emptySet(),
            selectedEquipment = emptySet(),
            selectedDifficulty = null
        )
        applyFilters()
    }

    private fun applyFilters() {
        val query = _uiState.value.searchQuery.lowercase()
        val bodyParts = _uiState.value.selectedBodyParts
        val equipment = _uiState.value.selectedEquipment
        val difficulty = _uiState.value.selectedDifficulty

        val filtered = _uiState.value.exercises.filter { exercise ->
            // Filtre par recherche
            val matchesSearch = query.isEmpty() ||
                    exercise.name.lowercase().contains(query) ||
                    exercise.target.lowercase().contains(query)

            // Filtre par groupe musculaire
            val matchesBodyPart = bodyParts.isEmpty() ||
                    exercise.bodyPart in bodyParts

            // Filtre par équipement
            val matchesEquipment = equipment.isEmpty() ||
                    exercise.equipment in equipment

            // Filtre par difficulté
            val matchesDifficulty = difficulty == null ||
                    exercise.difficulty == difficulty

            matchesSearch && matchesBodyPart && matchesEquipment && matchesDifficulty
        }

        _uiState.value = _uiState.value.copy(filteredExercises = filtered)
    }
}