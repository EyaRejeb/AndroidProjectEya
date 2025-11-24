package com.example.workoutapp.domain.model

data class Exercise(
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
) {
    fun getImageUrl(resolution: Int = 180): String {
        return "https://exercisedb.p.rapidapi.com/image?exerciseId=$id&resolution=$resolution&rapidapi-key=aeb7aec2f0mshb780d5ec7ac5f57p1518fajsn22f9a5472dee"
    }
}