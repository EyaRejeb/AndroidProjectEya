package com.example.workoutapp

import android.app.Application
import com.example.workoutapp.data.local.database.WorkoutDatabase
import com.example.workoutapp.data.remote.api.ExerciseApi
import com.example.workoutapp.data.remote.api.KtorClient
import com.example.workoutapp.data.repository.ExerciseRepository
import com.example.workoutapp.di.ViewModelFactory

class WorkoutApplication : Application() {

    private val database by lazy { WorkoutDatabase.getDatabase(this) }
    private val httpClient by lazy { KtorClient.create() }
    private val api by lazy { ExerciseApi(httpClient) }

    val repository by lazy {
        ExerciseRepository(api, database.exerciseDao())
    }

    val viewModelFactory by lazy {
        ViewModelFactory(repository)
    }

    override fun onTerminate() {
        super.onTerminate()
        httpClient.close()
    }
}