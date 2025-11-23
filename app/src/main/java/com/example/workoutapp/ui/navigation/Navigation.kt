package com.example.workoutapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.workoutapp.di.ViewModelFactory
import com.example.workoutapp.ui.screens.exercisedetail.ExerciseDetailScreen
import com.example.workoutapp.ui.screens.exercisedetail.ExerciseDetailViewModel
import com.example.workoutapp.ui.screens.exerciselist.ExerciseListScreen
import com.example.workoutapp.ui.screens.exerciselist.ExerciseListViewModel
import com.example.workoutapp.ui.screens.home.HomeScreen
import com.example.workoutapp.ui.screens.home.HomeViewModel

sealed class Screen(val route: String) {
    data object Home : Screen("home")
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
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            val viewModel: HomeViewModel = viewModel(factory = viewModelFactory)
            HomeScreen(
                viewModel = viewModel,
                onBodyPartClick = { bodyPart ->
                    navController.navigate(Screen.ExerciseList.createRoute(bodyPart))
                }
            )
        }

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