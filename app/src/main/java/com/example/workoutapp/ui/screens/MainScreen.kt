package com.example.workoutapp.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.workoutapp.ui.navigation.BottomNavItem
import com.example.workoutapp.ui.navigation.bottomNavItems
import com.example.workoutapp.ui.screens.favorites.FavoritesScreen
import com.example.workoutapp.presentation.viewmodels.favorites.FavoritesViewModel
import com.example.workoutapp.ui.screens.home.HomeScreen
import com.example.workoutapp.presentation.viewmodels.home.HomeViewModel
import com.example.workoutapp.ui.screens.profile.ProfileScreen
import com.example.workoutapp.presentation.viewmodels.profile.ProfileViewModel
import com.example.workoutapp.ui.screens.search.SearchScreen
import com.example.workoutapp.presentation.viewmodels.search.SearchViewModel
import com.example.workoutapp.ui.screens.statistics.StatisticsScreen
import com.example.workoutapp.presentation.viewmodels.statistics.StatisticsViewModel

@Composable
fun MainScreen(
    homeViewModel: HomeViewModel,
    favoritesViewModel: FavoritesViewModel,
    searchViewModel: SearchViewModel,
    statisticsViewModel: StatisticsViewModel,
    profileViewModel: ProfileViewModel,
    onNavigateToExerciseList: (String) -> Unit,
    onNavigateToExerciseDetail: (String) -> Unit
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = if (currentDestination?.hierarchy?.any {
                                        it.route == item.route
                                    } == true) {
                                    item.selectedIcon
                                } else {
                                    item.unselectedIcon
                                },
                                contentDescription = item.title
                            )
                        },
                        label = { Text(item.title) },
                        selected = currentDestination?.hierarchy?.any {
                            it.route == item.route
                        } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Home.route) {
                HomeScreen(
                    viewModel = homeViewModel,
                    onBodyPartClick = onNavigateToExerciseList
                )
            }

            composable(BottomNavItem.Favorites.route) {
                FavoritesScreen(
                    viewModel = favoritesViewModel,
                    onExerciseClick = onNavigateToExerciseDetail
                )
            }

            composable(BottomNavItem.Search.route) {
                SearchScreen(
                    viewModel = searchViewModel,
                    onExerciseClick = onNavigateToExerciseDetail
                )
            }

            composable(BottomNavItem.Statistics.route) {
                StatisticsScreen(
                    viewModel = statisticsViewModel
                )
            }

            composable(BottomNavItem.Profile.route) {
                ProfileScreen(
                    viewModel = profileViewModel
                )
            }
        }
    }
}