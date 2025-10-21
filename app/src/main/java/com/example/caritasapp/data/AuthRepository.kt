package com.example.caritasapp.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json

class AuthRepository(
    private val apiService: ApiService, 
    private val onlineApiService: ApiService,
    private val sessionManager: SessionManager
) {
    
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
            
            val response = NetworkModule.handleApiCall { apiService.signup(request) }
            
            println("Signup Response: ${response.message}")
            println("User ID from signup: ${response.backendResponse.id}")
            println("User Name: ${response.backendResponse.firstName} ${response.backendResponse.lastName}")
            
            // Store the user ID temporarily for use during confirmation
            sessionManager.saveTempUserId(response.backendResponse.id)
            
            // For signup, we don't get user data immediately - just confirmation
            // The user will be created after confirmation
            Result.success(response)
        } catch (e: Exception) {
            val errorMessage = when (e) {
                is ApiException -> e.errorMessage
                else -> e.message ?: "Network error"
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
            val response = NetworkModule.handleApiCall { apiService.login(request) }
            
            println("Login Response: User ID = ${response.userId}")
            println("Login Response: Message = ${response.message}")
            
            // Store the tokens from the response
            sessionManager.saveTokens(
                idToken = response.idToken,
                accessToken = response.accessToken,
                refreshToken = response.refreshToken
            )
            
            // Store the user data
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
            val errorMessage = when (e) {
                is ApiException -> e.errorMessage
                else -> e.message ?: "Network error"
            }
            _error.value = errorMessage
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
            
            val response = NetworkModule.handleApiCall { onlineApiService.confirmSignup(request) }
            
            println("Confirm Response: User ID = ${response.userId}")
            println("Confirm Response: Message = ${response.message}")
            
            // Store the tokens from the response
            sessionManager.saveTokens(
                idToken = response.idToken,
                accessToken = response.accessToken,
                refreshToken = response.refreshToken
            )
            
            // Store the user data
            // Use the stored user ID from signup, or fallback to response userId
            val userId = sessionManager.getTempUserId() ?: response.userId ?: "temp-user-id"
            val userData = UserData(
                id = userId,
                phoneNumber = phoneNumber,
                firstName = "User", // We don't have this info yet
                lastName = "", // We don't have this info yet
                email = null
            )
            sessionManager.saveUser(userData)
            _isLoggedIn.value = true // User is now logged in with tokens
            _currentUser.value = userData
            
            // Clear the temporary user ID since we now have the full user data
            sessionManager.clearTempUserId()
            
            _isLoading.value = false
            Result.success(response)
        } catch (e: Exception) {
            val errorMessage = when (e) {
                is ApiException -> e.errorMessage
                else -> e.message ?: "Network error"
            }
            _error.value = errorMessage
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
    
    suspend fun refreshTokens(): Result<RefreshTokenResponse> {
        return try {
            val userId = sessionManager.getUser()?.id ?: throw Exception("No user found")
            val refreshToken = sessionManager.getRefreshToken() ?: throw Exception("No refresh token found")
            
            val request = RefreshTokenRequest(
                userId = userId,
                refreshToken = refreshToken
            )
            
            val response = NetworkModule.handleApiCall { onlineApiService.refreshToken(request) }
            
            // Store the new tokens
            sessionManager.saveTokens(
                idToken = response.idToken,
                accessToken = response.accessToken,
                refreshToken = response.refreshToken
            )
            
            Result.success(response)
        } catch (e: Exception) {
            val errorMessage = when (e) {
                is ApiException -> e.errorMessage
                else -> e.message ?: "Network error"
            }
            _error.value = errorMessage
            Result.failure(e)
        }
    }
    
    fun clearError() {
        _error.value = null
    }
    
    companion object {
        @Volatile
        private var INSTANCE: AuthRepository? = null
        
        fun getInstance(apiService: ApiService, onlineApiService: ApiService, sessionManager: SessionManager): AuthRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AuthRepository(apiService, onlineApiService, sessionManager).also { INSTANCE = it }
            }
        }
    }
}
