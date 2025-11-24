package com.example.workoutapp.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.workoutapp.presentation.viewmodels.profile.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    var showThemeDialog by remember { mutableStateOf(false) }
    var showClearCacheDialog by remember { mutableStateOf(false) }
    var showClearFavoritesDialog by remember { mutableStateOf(false) }
    var showClearStatsDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil & Paramètres") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Section Apparence
            item {
                Text(
                    text = "Apparence",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            item {
                SettingItem(
                    icon = Icons.Filled.Palette,
                    title = "Thème",
                    subtitle = when (uiState.themeMode) {
                        "light" -> "Clair"
                        "dark" -> "Sombre"
                        else -> "Automatique (système)"
                    },
                    onClick = { showThemeDialog = true }
                )
            }

            // Section Favoris
            item {
                Text(
                    text = "Favoris",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            item {
                SettingItem(
                    icon = Icons.Filled.Favorite,
                    title = "Nombre de favoris",
                    subtitle = "${uiState.favoritesCount} exercice(s)",
                    onClick = null
                )
            }

            item {
                SettingItem(
                    icon = Icons.Filled.DeleteForever,
                    title = "Réinitialiser les favoris",
                    subtitle = "Supprimer tous les favoris",
                    onClick = { showClearFavoritesDialog = true },
                    isDestructive = true
                )
            }

            // Section Données
            item {
                Text(
                    text = "Données",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            item {
                SettingItem(
                    icon = Icons.Filled.Storage,
                    title = "Vider le cache",
                    subtitle = "Supprimer les exercices en cache",
                    onClick = { showClearCacheDialog = true }
                )
            }

            item {
                SettingItem(
                    icon = Icons.Filled.BarChart,
                    title = "Réinitialiser les statistiques",
                    subtitle = "Supprimer l'historique de consultation",
                    onClick = { showClearStatsDialog = true },
                    isDestructive = true
                )
            }

            // Section À propos
            item {
                Text(
                    text = "À propos",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            item {
                SettingItem(
                    icon = Icons.Filled.Info,
                    title = "À propos de l'application",
                    subtitle = "Version 1.0.0",
                    onClick = { showAboutDialog = true }
                )
            }
        }
    }

    // Dialogs
    if (showThemeDialog) {
        ThemeSelectionDialog(
            currentTheme = uiState.themeMode,
            onThemeSelected = {
                viewModel.setThemeMode(it)
                showThemeDialog = false
            },
            onDismiss = { showThemeDialog = false }
        )
    }

    if (showClearCacheDialog) {
        ConfirmationDialog(
            title = "Vider le cache ?",
            message = "Les exercices seront rechargés depuis l'API lors de votre prochaine visite.",
            onConfirm = {
                viewModel.clearCache()
                showClearCacheDialog = false
            },
            onDismiss = { showClearCacheDialog = false }
        )
    }

    if (showClearFavoritesDialog) {
        ConfirmationDialog(
            title = "Supprimer tous les favoris ?",
            message = "Cette action est irréversible.",
            onConfirm = {
                viewModel.clearAllFavorites()
                showClearFavoritesDialog = false
            },
            onDismiss = { showClearFavoritesDialog = false },
            isDestructive = true
        )
    }

    if (showClearStatsDialog) {
        ConfirmationDialog(
            title = "Réinitialiser les statistiques ?",
            message = "Tout l'historique de consultation sera supprimé.",
            onConfirm = {
                viewModel.clearStatistics()
                showClearStatsDialog = false
            },
            onDismiss = { showClearStatsDialog = false },
            isDestructive = true
        )
    }

    if (showAboutDialog) {
        AboutDialog(onDismiss = { showAboutDialog = false })
    }
}

@Composable
fun SettingItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: (() -> Unit)?,
    isDestructive: Boolean = false
) {
    Card(
        onClick = onClick ?: {},
        modifier = Modifier.fillMaxWidth(),
        enabled = onClick != null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isDestructive) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.primary
                }
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (isDestructive) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (onClick != null) {
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ThemeSelectionDialog(
    currentTheme: String,
    onThemeSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choisir le thème") },
        text = {
            Column {
                RadioButtonRow(
                    text = "Clair",
                    selected = currentTheme == "light",
                    onClick = { onThemeSelected("light") }
                )
                RadioButtonRow(
                    text = "Sombre",
                    selected = currentTheme == "dark",
                    onClick = { onThemeSelected("dark") }
                )
                RadioButtonRow(
                    text = "Automatique (système)",
                    selected = currentTheme == "system",
                    onClick = { onThemeSelected("system") }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Fermer")
            }
        }
    )
}

@Composable
fun RadioButtonRow(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text)
    }
}

@Composable
fun ConfirmationDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    isDestructive: Boolean = false
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    "Confirmer",
                    color = if (isDestructive) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}

@Composable
fun AboutDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(Icons.Filled.FitnessCenter, contentDescription = null)
        },
        title = { Text("WorkoutApp") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Version 1.0.0")
                Text("Une application complète pour découvrir et organiser vos exercices de fitness.")
                Text("Développé avec ❤️ en Kotlin & Jetpack Compose")
                Text("API: ExerciseDB")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Fermer")
            }
        }
    )
}