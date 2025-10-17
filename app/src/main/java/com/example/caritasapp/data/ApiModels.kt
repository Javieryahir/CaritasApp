package com.example.caritasapp.data

import kotlinx.serialization.Serializable

// Request models
@Serializable
data class SignupRequest(
    val phoneNumber: String,
    val password: String,
    val firstName: String,
    val lastName: String
)

@Serializable
data class LoginRequest(
    val phoneNumber: String,
    val password: String
)

// Response models
@Serializable
data class UserData(
    val id: String,
    val phoneNumber: String,
    val firstName: String,
    val lastName: String,
    val token: String? = null
)

@Serializable
data class LoginResponse(
    val message: String,
    val idToken: String,
    val accessToken: String,
    val refreshToken: String
)

@Serializable
data class SignupResponse(
    val message: String,
    val success: Boolean? = null
)

@Serializable
data class SignupConfirmRequest(
    val phoneNumber: String,
    val code: String
)

@Serializable
data class SignupConfirmResponse(
    val message: String,
    val idToken: String,
    val accessToken: String,
    val refreshToken: String
)
