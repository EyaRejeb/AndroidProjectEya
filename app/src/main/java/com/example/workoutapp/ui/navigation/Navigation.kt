package com.example.workoutapp.ui.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.workoutapp.di.ViewModelFactory
import com.example.workoutapp.ui.screens.MainScreen
import com.example.workoutapp.ui.screens.exercisedetail.ExerciseDetailScreen
import com.example.workoutapp.ui.screens.exercisedetail.ExerciseDetailViewModel
import com.example.workoutapp.ui.screens.exerciselist.ExerciseListScreen
import com.example.workoutapp.ui.screens.exerciselist.ExerciseListViewModel
import com.example.workoutapp.ui.screens.favorites.FavoritesViewModel
import com.example.workoutapp.ui.screens.home.HomeViewModel
import com.example.workoutapp.ui.screens.profile.ProfileViewModel
import com.example.workoutapp.ui.screens.search.SearchViewModel
import com.example.workoutapp.ui.screens.splash.SplashScreen
import com.example.workoutapp.ui.screens.statistics.StatisticsViewModel
import kotlinx.coroutines.delay

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Main : Screen("main")
    data object ExerciseList : Screen("exercise_list/{bodyPart}") {
        fun createRoute(bodyPart: String) = "exercise_list/$bodyPart"
    }
    data object ExerciseDetail : Screen("exercise_detail/{exerciseId}") {
        fun createRoute(exerciseId: String) = "exercise_detail/$exerciseId"
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    viewModelFactory: ViewModelFactory
) {
    var showSplash by remember { mutableStateOf(true) }

    NavHost(
        navController = navController,
        startDestination = if (showSplash) Screen.Splash.route else Screen.Main.route
    ) {
        // Splash Screen
        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashFinished = {
                    showSplash = false
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // Main Screen avec Bottom Navigation
        composable(Screen.Main.route) {
            val homeViewModel: HomeViewModel = viewModel(factory = viewModelFactory)
            val favoritesViewModel: FavoritesViewModel = viewModel(factory = viewModelFactory)
            val searchViewModel: SearchViewModel = viewModel(factory = viewModelFactory)
            val statisticsViewModel: StatisticsViewModel = viewModel(factory = viewModelFactory)
            val profileViewModel: ProfileViewModel = viewModel(factory = viewModelFactory)

            MainScreen(
                homeViewModel = homeViewModel,
                favoritesViewModel = favoritesViewModel,
                searchViewModel = searchViewModel,
                statisticsViewModel = statisticsViewModel,
                profileViewModel = profileViewModel,
                onNavigateToExerciseList = { bodyPart ->
                    navController.navigate(Screen.ExerciseList.createRoute(bodyPart))
                },
                onNavigateToExerciseDetail = { exerciseId ->
                    navController.navigate(Screen.ExerciseDetail.createRoute(exerciseId))
                }
            )
        }

        // Exercise List
        composable(
            route = Screen.ExerciseList.route,
            arguments = listOf(
                navArgument("bodyPart") { type = NavType.StringType }
            )
        ) {
            val viewModel: ExerciseListViewModel = viewModel(factory = viewModelFactory)
            ExerciseListScreen(
                viewModel = viewModel,
                onExerciseClick = { exerciseId ->
                    navController.navigate(Screen.ExerciseDetail.createRoute(exerciseId))
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        // Exercise Detail
        composable(
            route = Screen.ExerciseDetail.route,
            arguments = listOf(
                navArgument("exerciseId") { type = NavType.StringType }
            )
        ) {
            val viewModel: ExerciseDetailViewModel = viewModel(factory = viewModelFactory)
            ExerciseDetailScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}