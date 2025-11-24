package com.example.workoutapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercise_views")
data class ExerciseViewEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val exerciseId: String,
    val exerciseName: String,
    val bodyPart: String,
    val timestamp: Long = System.currentTimeMillis()
)