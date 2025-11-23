package com.example.workoutapp.data.repository

import com.example.workoutapp.data.local.entity.ExerciseEntity
import com.example.workoutapp.data.remote.dto.ExerciseDto
import com.example.workoutapp.domain.model.Exercise

fun ExerciseDto.toEntity(): ExerciseEntity {
    return ExerciseEntity(
        id = id,
        name = name,
        bodyPart = bodyPart,
        target = target,
        equipment = equipment,
        secondaryMuscles = secondaryMuscles,
        instructions = instructions,
        description = description,
        difficulty = difficulty,
        category = category,
        isFavorite = false
    )
}

fun ExerciseEntity.toDomain(): Exercise {
    return Exercise(
        id = id,
        name = name,
        bodyPart = bodyPart,
        target = target,
        equipment = equipment,
        secondaryMuscles = secondaryMuscles,
        instructions = instructions,
        description = description,
        difficulty = difficulty,
        category = category,
        isFavorite = isFavorite
    )
}

fun Exercise.toEntity(): ExerciseEntity {
    return ExerciseEntity(
        id = id,
        name = name,
        bodyPart = bodyPart,
        target = target,
        equipment = equipment,
        secondaryMuscles = secondaryMuscles,
        instructions = instructions,
        description = description,
        difficulty = difficulty,
        category = category,
        isFavorite = isFavorite
    )
}