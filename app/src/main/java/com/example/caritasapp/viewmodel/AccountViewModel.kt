package com.example.caritasapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caritasapp.data.AuthRepository
import com.example.caritasapp.data.UserRepository
import com.example.caritasapp.data.UserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AccountViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AccountUiState())
    val uiState: StateFlow<AccountUiState> = _uiState.asStateFlow()
    
    init {
        // Get current user from auth repository
        val currentUser = authRepository.currentUser.value
        if (currentUser != null && currentUser.id != "temp_id") {
            fetchUserData(currentUser.id)
        } else {
            // If we have a temp user, try to get the real user data
            _uiState.value = _uiState.value.copy(
                user = currentUser,
                isLoading = false
            )
        }
    }
    
    private fun fetchUserData(userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            val result = userRepository.fetchUserData(userId)
            
            result.fold(
                onSuccess = { userData ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        user = userData,
                        error = null
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Failed to load user data"
                    )
                }
            )
        }
    }
    
    fun logout() {
        println("=== ACCOUNT VIEWMODEL LOGOUT ===")
        println("Before logout - User: ${_uiState.value.user}")
        authRepository.logout()
        userRepository.clearUserData()
        _uiState.value = AccountUiState()
        println("After logout - User: ${_uiState.value.user}")
        println("=================================")
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class AccountUiState(
    val isLoading: Boolean = true,
    val user: UserData? = null,
    val error: String? = null
)
