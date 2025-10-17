package com.example.caritasapp.data

import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("api/signup")
    suspend fun signup(@Body request: SignupRequest): SignupResponse
    
    @POST("api/signup/confirm")
    suspend fun confirmSignup(@Body request: SignupConfirmRequest): SignupConfirmResponse
    
    @POST("api/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse
}
