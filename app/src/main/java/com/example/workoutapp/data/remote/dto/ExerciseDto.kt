package com.example.workoutapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ExerciseDto(
    val id: String,
    val name: String,
    val bodyPart: String,
    val target: String,
    val equipment: String,
    val secondaryMuscles: List<String>,
    val instructions: List<String>,
    val description: String? = null,
    val difficulty: String? = null,
    val category: String? = null
)