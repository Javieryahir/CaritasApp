package com.example.caritasapp.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json

class AuthRepository(private val apiService: ApiService, private val sessionManager: SessionManager) {
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _isLoggedIn = MutableStateFlow(sessionManager.isLoggedIn())
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()
    
    private val _currentUser = MutableStateFlow(sessionManager.getUser())
    val currentUser: StateFlow<UserData?> = _currentUser.asStateFlow()
    
    suspend fun signup(phoneNumber: String, firstName: String, lastName: String): Result<SignupResponse> {
        return try {
            _isLoading.value = true
            _error.value = null
            
            val request = SignupRequest(
                phoneNumber = phoneNumber,
                firstName = firstName,
                lastName = lastName
            )
            
            println("=== SIGNUP DEBUG ===")
            println("Phone Number: '$phoneNumber'")
            println("First Name: '$firstName'")
            println("Last Name: '$lastName'")
            println("===================")
            
            val response = apiService.signup(request)
            
            println("Signup Response: ${response.message}")
            
            // For signup, we don't get user data immediately - just confirmation
            // The user will be created after confirmation
            Result.success(response)
        } catch (e: Exception) {
            val errorMessage = when (e) {
                is ApiException -> e.errorMessage
                else -> {
                    when {
                        e.message?.contains("User already exists") == true -> "User already exists. Please try logging in instead."
                        e.message?.contains("400") == true -> "Invalid request data. Please check your information."
                        e.message?.contains("401") == true -> "Unauthorized. Please try again."
                        e.message?.contains("404") == true -> "Service not found. Please try again later."
                        e.message?.contains("500") == true -> "Server error. Please try again later."
                        else -> e.message ?: "Network error"
                    }
                }
            }
            _error.value = errorMessage
            println("Signup Error: ${e.message}")
            Result.failure(e)
        } finally {
            _isLoading.value = false
        }
    }
    
    suspend fun login(phoneNumber: String): Result<LoginResponse> {
        return try {
            _isLoading.value = true
            _error.value = null
            
            val request = LoginRequest(
                phoneNumber = phoneNumber
            )
            val response = apiService.login(request)
            
            println("Login Response: User ID = ${response.userId}")
            
            // Store the userId from the response
            // Note: The new API only returns userId, no tokens
            // The user is considered logged in with just the userId
            val userData = UserData(
                id = response.userId,
                phoneNumber = phoneNumber,
                firstName = "User", // We don't have this info yet
                lastName = "", // We don't have this info yet
                email = null
            )
            sessionManager.saveUser(userData)
            _isLoggedIn.value = true
            _currentUser.value = userData
            Result.success(response)
        } catch (e: Exception) {
            _error.value = e.message ?: "Network error"
            Result.failure(e)
        } finally {
            _isLoading.value = false
        }
    }
    
    suspend fun confirmSignup(phoneNumber: String, code: String): Result<SignupConfirmResponse> {
        return try {
            _isLoading.value = true
            _error.value = null
            
            val request = SignupConfirmRequest(
                phoneNumber = phoneNumber,
                code = code
            )
            
            val response = apiService.confirmSignup(request)
            
            println("Confirm Response: User ID = ${response.userId}")
            
            // Store the userId from the response
            // Note: The new API only returns userId, no tokens
            // The user will need to login separately to get tokens
            val userData = UserData(
                id = response.userId,
                phoneNumber = phoneNumber,
                firstName = "User", // We don't have this info yet
                lastName = "", // We don't have this info yet
                email = null
            )
            sessionManager.saveUser(userData)
            _isLoggedIn.value = false // User is not fully logged in until they login
            _currentUser.value = userData
            
            _isLoading.value = false
            Result.success(response)
        } catch (e: Exception) {
            _error.value = e.message ?: "Network error"
            Result.failure(e)
        } finally {
            _isLoading.value = false
        }
    }
    
    fun logout() {
        println("=== AUTH REPOSITORY LOGOUT ===")
        println("Before logout - isLoggedIn: ${_isLoggedIn.value}")
        println("Before logout - currentUser: ${_currentUser.value}")
        sessionManager.logout()
        _isLoggedIn.value = false
        _currentUser.value = null
        println("After logout - isLoggedIn: ${_isLoggedIn.value}")
        println("After logout - currentUser: ${_currentUser.value}")
        println("===============================")
    }
    
    fun clearError() {
        _error.value = null
    }
    
    companion object {
        @Volatile
        private var INSTANCE: AuthRepository? = null
        
        fun getInstance(apiService: ApiService, sessionManager: SessionManager): AuthRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AuthRepository(apiService, sessionManager).also { INSTANCE = it }
            }
        }
    }
}
