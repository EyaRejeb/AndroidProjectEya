package com.example.workoutapp.data.local.dao

import androidx.room.*
import com.example.workoutapp.data.local.entity.ExerciseViewEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseViewDao {

    @Insert
    suspend fun insertView(view: ExerciseViewEntity)

    @Query("SELECT COUNT(*) FROM exercise_views")
    fun getTotalViewsCount(): Flow<Int>

    @Query("SELECT COUNT(DISTINCT exerciseId) FROM exercise_views")
    fun getUniqueExercisesViewed(): Flow<Int>

    @Query("SELECT bodyPart, COUNT(*) as count FROM exercise_views GROUP BY bodyPart")
    fun getViewsByBodyPart(): Flow<Map<String, Int>>

    @Query("""
        SELECT exerciseId, exerciseName, bodyPart, COUNT(*) as viewCount 
        FROM exercise_views 
        GROUP BY exerciseId 
        ORDER BY viewCount DESC 
        LIMIT 5
    """)
    fun getTopExercises(): Flow<List<TopExercise>>

    @Query("DELETE FROM exercise_views")
    suspend fun clearAllViews()
}

data class TopExercise(
    val exerciseId: String,
    val exerciseName: String,
    val bodyPart: String,
    val viewCount: Int
)