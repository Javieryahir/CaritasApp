package com.example.caritasapp.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PersonRepository private constructor(
    private val apiService: ApiService
) {
    companion object {
        @Volatile
        private var INSTANCE: PersonRepository? = null

        fun getInstance(apiService: ApiService): PersonRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: PersonRepository(apiService).also { INSTANCE = it }
            }
        }
    }

    suspend fun createPerson(personRequest: PersonRequest): Flow<PersonResponse?> = flow {
        try {
            val response = apiService.createPerson(personRequest)
            emit(response)
        } catch (e: Exception) {
            println("❌ Error creating person: ${e.message}")
            emit(null)
        }
    }

    suspend fun createReservation(reservationRequest: NewReservationRequest): Flow<NewReservationResponse?> = flow {
        try {
            val response = apiService.createNewReservation(reservationRequest)
            emit(response)
        } catch (e: Exception) {
            println("❌ Error creating reservation: ${e.message}")
            emit(null)
        }
    }
}
