package com.example.workoutapp.di

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.workoutapp.data.local.database.WorkoutDatabase
import com.example.workoutapp.data.preferences.PreferencesManager
import com.example.workoutapp.data.remote.api.ExerciseApi
import com.example.workoutapp.data.repository.ExerciseRepository
import com.example.workoutapp.ui.screens.exercisedetail.ExerciseDetailViewModel
import com.example.workoutapp.ui.screens.exerciselist.ExerciseListViewModel
import com.example.workoutapp.ui.screens.favorites.FavoritesViewModel
import com.example.workoutapp.ui.screens.home.HomeViewModel
import com.example.workoutapp.ui.screens.profile.ProfileViewModel
import com.example.workoutapp.ui.screens.search.SearchViewModel
import com.example.workoutapp.ui.screens.statistics.StatisticsViewModel

class ViewModelFactory(
    private val repository: ExerciseRepository,
    private val api: ExerciseApi,
    private val preferencesManager: PreferencesManager,
    private val database: WorkoutDatabase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val savedStateHandle = extras.createSavedStateHandle()

        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(repository) as T
            }
            modelClass.isAssignableFrom(FavoritesViewModel::class.java) -> {
                FavoritesViewModel(repository) as T
            }
            modelClass.isAssignableFrom(SearchViewModel::class.java) -> {
                SearchViewModel(api) as T
            }
            modelClass.isAssignableFrom(StatisticsViewModel::class.java) -> {
                StatisticsViewModel(repository, preferencesManager) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(repository, preferencesManager, database) as T
            }
            modelClass.isAssignableFrom(ExerciseListViewModel::class.java) -> {
                ExerciseListViewModel(repository, savedStateHandle) as T
            }
            modelClass.isAssignableFrom(ExerciseDetailViewModel::class.java) -> {
                ExerciseDetailViewModel(repository, savedStateHandle) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}