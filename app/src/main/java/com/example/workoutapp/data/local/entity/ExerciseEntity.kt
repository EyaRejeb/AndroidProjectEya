package com.example.workoutapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.workoutapp.data.local.Converters

@Entity(tableName = "exercises")
@TypeConverters(Converters::class)
data class ExerciseEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val bodyPart: String,
    val target: String,
    val equipment: String,
    val secondaryMuscles: List<String>,
    val instructions: List<String>,
    val description: String?,
    val difficulty: String?,
    val category: String?,
    val isFavorite: Boolean = false
)