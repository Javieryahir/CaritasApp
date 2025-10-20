package com.example.caritasapp.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserRepository(private val apiService: ApiService) {
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _userData = MutableStateFlow<UserData?>(null)
    val userData: StateFlow<UserData?> = _userData.asStateFlow()
    
    suspend fun fetchUserData(userId: String): Result<UserData> {
        return try {
            _isLoading.value = true
            _error.value = null
            
            val userData = apiService.getUser(userId)
            _userData.value = userData
            Result.success(userData)
        } catch (e: Exception) {
            _error.value = e.message ?: "Failed to fetch user data"
            Result.failure(e)
        } finally {
            _isLoading.value = false
        }
    }
    
    fun clearError() {
        _error.value = null
    }
    
    fun clearUserData() {
        _userData.value = null
    }
    
    companion object {
        @Volatile
        private var INSTANCE: UserRepository? = null
        
        fun getInstance(apiService: ApiService): UserRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserRepository(apiService).also { INSTANCE = it }
            }
        }
    }
}
