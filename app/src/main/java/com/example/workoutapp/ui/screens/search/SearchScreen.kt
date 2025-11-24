package com.example.workoutapp.ui.screens.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.workoutapp.presentation.viewmodels.search.SearchUiState
import com.example.workoutapp.presentation.viewmodels.search.SearchViewModel
import com.example.workoutapp.ui.components.ExerciseCard
import com.example.workoutapp.ui.components.LoadingView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onExerciseClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showFilters by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recherche") },
                actions = {
                    IconButton(onClick = { showFilters = !showFilters }) {
                        Badge(
                            containerColor = if (hasActiveFilters(uiState)) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            }
                        ) {
                            Icon(Icons.Filled.FilterList, "Filtres")
                        }
                    }

                    if (hasActiveFilters(uiState)) {
                        IconButton(onClick = { viewModel.clearFilters() }) {
                            Icon(Icons.Filled.Clear, "Effacer filtres")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Barre de recherche
            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = { viewModel.onSearchQueryChange(it) },
                modifier = Modifier.padding(16.dp)
            )

            // Filtres (affichés si showFilters est true)
            if (showFilters) {
                FilterSection(
                    uiState = uiState,
                    onBodyPartToggle = { viewModel.toggleBodyPartFilter(it) },
                    onEquipmentToggle = { viewModel.toggleEquipmentFilter(it) },
                    onDifficultySelect = { viewModel.setDifficultyFilter(it) }
                )
            }

            // Résultats
            when {
                uiState.isLoading -> {
                    LoadingView()
                }
                uiState.error != null -> {
                    ErrorMessage(uiState.error!!)
                }
                uiState.filteredExercises.isEmpty() -> {
                    NoResultsView()
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                text = "${uiState.filteredExercises.size} exercice(s) trouvé(s)",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        items(uiState.filteredExercises, key = { it.id }) { exercise ->
                            ExerciseCard(
                                exercise = exercise,
                                onClick = { onExerciseClick(exercise.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text("Rechercher un exercice...") },
        leadingIcon = {
            Icon(Icons.Filled.Search, contentDescription = null)
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Filled.Clear, contentDescription = "Effacer")
                }
            }
        },
        singleLine = true
    )
}

@Composable
fun FilterSection(
    uiState: SearchUiState,
    onBodyPartToggle: (String) -> Unit,
    onEquipmentToggle: (String) -> Unit,
    onDifficultySelect: (String?) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Filtres",
                style = MaterialTheme.typography.titleMedium
            )

            // Groupes musculaires
            Text("Groupes musculaires", style = MaterialTheme.typography.labelMedium)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.availableBodyParts) { bodyPart ->
                    FilterChip(
                        selected = bodyPart in uiState.selectedBodyParts,
                        onClick = { onBodyPartToggle(bodyPart) },
                        label = { Text(bodyPart) }
                    )
                }
            }

            // Équipements
            Text("Équipement", style = MaterialTheme.typography.labelMedium)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.availableEquipment) { equipment ->
                    FilterChip(
                        selected = equipment in uiState.selectedEquipment,
                        onClick = { onEquipmentToggle(equipment) },
                        label = { Text(equipment) }
                    )
                }
            }

            // Difficulté
            Text("Difficulté", style = MaterialTheme.typography.labelMedium)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("beginner", "intermediate", "advanced").forEach { difficulty ->
                    FilterChip(
                        selected = uiState.selectedDifficulty == difficulty,
                        onClick = {
                            onDifficultySelect(
                                if (uiState.selectedDifficulty == difficulty) null else difficulty
                            )
                        },
                        label = { Text(difficulty) }
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun NoResultsView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.SearchOff,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Aucun résultat",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Essayez d'autres mots-clés ou filtres",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ErrorMessage(message: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Erreur",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.error
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}

fun hasActiveFilters(uiState: SearchUiState): Boolean {
    return uiState.selectedBodyParts.isNotEmpty() ||
            uiState.selectedEquipment.isNotEmpty() ||
            uiState.selectedDifficulty != null
}