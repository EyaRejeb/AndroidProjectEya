package com.example.workoutapp

import android.app.Application
import com.example.workoutapp.data.local.database.WorkoutDatabase
import com.example.workoutapp.data.preferences.PreferencesManager
import com.example.workoutapp.data.remote.api.ExerciseApi
import com.example.workoutapp.data.remote.api.KtorClient
import com.example.workoutapp.data.repository.ExerciseRepository
import com.example.workoutapp.di.ViewModelFactory

class WorkoutApplication : Application() {

    private val database by lazy { WorkoutDatabase.getDatabase(this) }
    private val httpClient by lazy { KtorClient.create() }
    private val api by lazy { ExerciseApi(httpClient) }

    val preferencesManager by lazy { PreferencesManager(this) }

    val repository by lazy {
        ExerciseRepository(api, database.exerciseDao())
    }

    val viewModelFactory by lazy {
        ViewModelFactory(
            repository = repository,
            api = api,
            preferencesManager = preferencesManager,
            database = database
        )
    }

    override fun onTerminate() {
        super.onTerminate()
        httpClient.close()
    }
}