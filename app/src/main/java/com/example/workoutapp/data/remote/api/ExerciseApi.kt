package com.example.workoutapp.data.remote.api

import com.example.workoutapp.data.remote.dto.ExerciseDto
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class ExerciseApi(private val client: HttpClient) {

    companion object {
        private const val BASE_URL = "https://exercisedb.p.rapidapi.com"
        private const val API_KEY = "aeb7aec2f0mshb780d5ec7ac5f57p1518fajsn22f9a5472dee"
    }

    suspend fun getExercises(
        limit: Int = 0,
        offset: Int = 0
    ): List<ExerciseDto> {
        return client.get("$BASE_URL/exercises") {
            parameter("limit", limit)
            parameter("offset", offset)
            parameter("rapidapi-key", API_KEY)
        }.body()
    }

    suspend fun getExercisesByBodyPart(
        bodyPart: String,
        limit: Int = 0,
        offset: Int = 0
    ): List<ExerciseDto> {
        return client.get("$BASE_URL/exercises/bodyPart/$bodyPart") {
            parameter("limit", limit)
            parameter("offset", offset)
            parameter("rapidapi-key", API_KEY)
        }.body()
    }

    suspend fun getExerciseById(id: String): ExerciseDto {
        return client.get("$BASE_URL/exercises/exercise/$id") {
            parameter("rapidapi-key", API_KEY)
        }.body()
    }

    suspend fun getBodyPartList(): List<String> {
        return client.get("$BASE_URL/exercises/bodyPartList") {
            parameter("rapidapi-key", API_KEY)
        }.body()
    }

    suspend fun getEquipmentList(): List<String> {
        return client.get("$BASE_URL/exercises/equipmentList") {
            parameter("rapidapi-key", API_KEY)
        }.body()
    }

    suspend fun getTargetList(): List<String> {
        return client.get("$BASE_URL/exercises/targetList") {
            parameter("rapidapi-key", API_KEY)
        }.body()
    }
}