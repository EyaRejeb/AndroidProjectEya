package com.example.workoutapp.di

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.workoutapp.data.repository.ExerciseRepository
import com.example.workoutapp.ui.screens.exercisedetail.ExerciseDetailViewModel
import com.example.workoutapp.ui.screens.exerciselist.ExerciseListViewModel
import com.example.workoutapp.ui.screens.home.HomeViewModel

class ViewModelFactory(
    private val repository: ExerciseRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val savedStateHandle = extras.createSavedStateHandle()

        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(repository) as T
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