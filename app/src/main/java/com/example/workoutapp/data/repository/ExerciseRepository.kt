package com.example.workoutapp.data.repository

import com.example.workoutapp.data.local.dao.ExerciseDao
import com.example.workoutapp.data.remote.api.ExerciseApi
import com.example.workoutapp.domain.model.Exercise
import com.example.workoutapp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class ExerciseRepository(
    private val api: ExerciseApi,
    private val dao: ExerciseDao
) {

    fun getExercisesByBodyPart(bodyPart: String): Flow<Resource<List<Exercise>>> = flow {
        emit(Resource.Loading())

        try {
            // Essayer d'abord de charger depuis la base locale
            val localExercises = dao.getExercisesByBodyPart(bodyPart)
            var hasCachedData = false

            localExercises.collect { cachedList ->
                if (cachedList.isNotEmpty()) {
                    hasCachedData = true
                    emit(Resource.Success(cachedList.map { it.toDomain() }))
                }
            }

            // Essayer de récupérer depuis l'API
            try {
                val remoteExercises = api.getExercisesByBodyPart(bodyPart)
                val entities = remoteExercises.map { it.toEntity() }
                dao.insertExercises(entities)
            } catch (e: Exception) {
                if (!hasCachedData) {
                    emit(Resource.Error("Erreur de connexion: ${e.localizedMessage}"))
                }
            }

        } catch (e: Exception) {
            emit(Resource.Error("Erreur: ${e.localizedMessage}"))
        }
    }

    fun getExerciseById(id: String): Flow<Resource<Exercise>> = flow {
        emit(Resource.Loading())

        try {
            val localExercise = dao.getExerciseById(id)

            if (localExercise != null) {
                emit(Resource.Success(localExercise.toDomain()))
            }

            try {
                val remoteExercise = api.getExerciseById(id)
                val entity = remoteExercise.toEntity().copy(
                    isFavorite = localExercise?.isFavorite ?: false
                )
                dao.insertExercise(entity)
                emit(Resource.Success(entity.toDomain()))
            } catch (e: Exception) {
                if (localExercise == null) {
                    emit(Resource.Error("Erreur de connexion: ${e.localizedMessage}"))
                }
            }

        } catch (e: Exception) {
            emit(Resource.Error("Erreur: ${e.localizedMessage}"))
        }
    }

    suspend fun getBodyPartList(): Resource<List<String>> {
        return try {
            val bodyParts = api.getBodyPartList()
            Resource.Success(bodyParts)
        } catch (e: Exception) {
            Resource.Error("Erreur: ${e.localizedMessage}")
        }
    }

    fun getFavoriteExercises(): Flow<List<Exercise>> {
        return dao.getFavoriteExercises().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun toggleFavorite(exerciseId: String, isFavorite: Boolean) {
        dao.updateFavoriteStatus(exerciseId, isFavorite)
    }
}