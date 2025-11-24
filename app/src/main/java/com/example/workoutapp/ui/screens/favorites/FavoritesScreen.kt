package com.example.workoutapp.ui.screens.favorites

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.workoutapp.domain.model.Exercise
import com.example.workoutapp.ui.components.ExerciseCard
import com.example.workoutapp.ui.components.LoadingView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel,
    onExerciseClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showMenu by remember { mutableStateOf(false) }
    var showClearDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Mes Favoris (${uiState.favorites.size})")
                },
                actions = {
                    if (uiState.favorites.isNotEmpty()) {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Filled.MoreVert, "Menu")
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Grouper par groupe musculaire") },
                                onClick = {
                                    viewModel.setGroupBy(GroupBy.BODY_PART)
                                    showMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Grouper par équipement") },
                                onClick = {
                                    viewModel.setGroupBy(GroupBy.EQUIPMENT)
                                    showMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Sans groupement") },
                                onClick = {
                                    viewModel.setGroupBy(GroupBy.NONE)
                                    showMenu = false
                                }
                            )
                            Divider()
                            DropdownMenuItem(
                                text = { Text("Tout supprimer", color = MaterialTheme.colorScheme.error) },
                                onClick = {
                                    showClearDialog = true
                                    showMenu = false
                                },
                                leadingIcon = {
                                    Icon(Icons.Filled.DeleteForever, null, tint = MaterialTheme.colorScheme.error)
                                }
                            )
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    LoadingView()
                }
                uiState.favorites.isEmpty() -> {
                    EmptyFavoritesView()
                }
                else -> {
                    FavoritesList(
                        exercises = uiState.favorites,
                        groupBy = uiState.groupBy,
                        onExerciseClick = onExerciseClick,
                        onRemoveFavorite = { viewModel.removeFavorite(it) }
                    )
                }
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Supprimer tous les favoris ?") },
            text = { Text("Cette action est irréversible. Tous vos favoris seront supprimés.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAllFavorites()
                        showClearDialog = false
                    }
                ) {
                    Text("Supprimer", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }
}

@Composable
fun EmptyFavoritesView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.FavoriteBorder,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Aucun favori",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Ajoutez des exercices à vos favoris pour les retrouver facilement ici",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun FavoritesList(
    exercises: List<Exercise>,
    groupBy: GroupBy,
    onExerciseClick: (String) -> Unit,
    onRemoveFavorite: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        when (groupBy) {
            GroupBy.NONE -> {
                items(exercises, key = { it.id }) { exercise ->
                    ExerciseCard(
                        exercise = exercise,
                        onClick = { onExerciseClick(exercise.id) }
                    )
                }
            }
            GroupBy.BODY_PART -> {
                val grouped = exercises.groupBy { it.bodyPart }
                grouped.forEach { (bodyPart, groupExercises) ->
                    item {
                        Text(
                            text = bodyPart.uppercase(),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    items(groupExercises, key = { it.id }) { exercise ->
                        ExerciseCard(
                            exercise = exercise,
                            onClick = { onExerciseClick(exercise.id) }
                        )
                    }
                }
            }
            GroupBy.EQUIPMENT -> {
                val grouped = exercises.groupBy { it.equipment }
                grouped.forEach { (equipment, groupExercises) ->
                    item {
                        Text(
                            text = equipment.uppercase(),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    items(groupExercises, key = { it.id }) { exercise ->
                        ExerciseCard(
                            exercise = exercise,
                            onClick = { onExerciseClick(exercise.id) }
                        )
                    }
                }
            }
            GroupBy.DIFFICULTY -> {
                val grouped = exercises.groupBy { it.difficulty ?: "Unknown" }
                grouped.forEach { (difficulty, groupExercises) ->
                    item {
                        Text(
                            text = difficulty.uppercase(),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    items(groupExercises, key = { it.id }) { exercise ->
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