package com.example.caritasapp.reservations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caritasapp.data.HostelRepository
import com.example.caritasapp.data.remote.NetworkModule
import com.example.caritasapp.data.remote.dto.HostelDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.caritasapp.BuildConfig

sealed interface HostelsLoadState {
    object Idle : HostelsLoadState
    object Loading : HostelsLoadState
    data class Success(val items: List<HostelDto>) : HostelsLoadState
    data class Error(val message: String) : HostelsLoadState
}

class ReservationsViewModel : ViewModel() {

    private val api = NetworkModule.provideApi(BuildConfig.BASE_URL)
    private val repo = HostelRepository(api)

    private val _state = MutableStateFlow<HostelsLoadState>(HostelsLoadState.Idle)
    val state: StateFlow<HostelsLoadState> = _state

    /** Llama al endpoint con los parámetros requeridos */
    fun loadHostels(
        startDate: String,     // "YYYY-MM-DD"
        endDate: String,       // "YYYY-MM-DD"
        filtersCsv: String?    // "laundry,meal,breakfast" o null
    ) {
        viewModelScope.launch {
            _state.value = HostelsLoadState.Loading
            repo.getHostels(startDate, endDate, filtersCsv)
                .onSuccess { _state.value = HostelsLoadState.Success(it) }
                .onFailure { _state.value = HostelsLoadState.Error(it.message ?: "Error de red") }
        }
    }
}
