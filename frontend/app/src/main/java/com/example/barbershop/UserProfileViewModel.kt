package com.example.barbershop

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.barbershop.network.AppointmentResponse
import com.example.barbershop.network.NetworkClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

data class UserProfileUiState(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val upcomingAppointments: List<AppointmentResponse> = emptyList(),
    val historyAppointments: List<AppointmentResponse> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class UserProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UserProfileUiState())
    val uiState: StateFlow<UserProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            NetworkClient.authState.collect { authState ->
                when (authState) {
                    is NetworkClient.AuthState.LoggedIn -> {
                        _uiState.update {
                            it.copy(
                                name = authState.name,
                                email = authState.email,
                                phone = authState.phone,
                                isLoading = false
                            )
                        }
                        loadUpcomingAppointments()
                        loadAppointmentHistory()
                    }
                    is NetworkClient.AuthState.LoggedOut -> {
                        delay(750)
                        _uiState.update { UserProfileUiState() }
                    }
                }
            }
        }
    }

    fun loadUpcomingAppointments() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val response = NetworkClient.appointmentApi.getUpcomingAppointments()
                if (response.isSuccessful) {
                    val appointments = response.body() ?: emptyList()
                    // Order by appointment time (startTime)
                    val sortedAppointments = appointments.sortedBy { it.startTime }
                    _uiState.update { it.copy(upcomingAppointments = sortedAppointments, isLoading = false) }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Failed to load upcoming appointments") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Connection error") }
            }
        }
    }

    fun loadAppointmentHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = NetworkClient.appointmentApi.getAppointmentHistory()
                if (response.isSuccessful) {
                    val appointments = response.body() ?: emptyList()
                    // Order by appointment time (descending - most recent first)
                    val sortedAppointments = appointments.sortedByDescending { it.startTime }
                    _uiState.update { it.copy(historyAppointments = sortedAppointments) }
                }
            } catch (e: Exception) {
                Log.e("UserProfileViewModel", "Error loading history", e)
            }
        }
    }

    fun cancelAppointment(appointmentId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = NetworkClient.appointmentApi.cancelAppointment(appointmentId)
                if (response.isSuccessful) {
                    // Refresh the lists after successful cancellation
                    loadUpcomingAppointments()
                    loadAppointmentHistory()
                } else {
                    Log.e("UserProfileViewModel", "Failed to cancel appointment: ${response.code()}")
                    _uiState.update { it.copy(errorMessage = "Failed to cancel appointment") }
                }
            } catch (e: Exception) {
                Log.e("UserProfileViewModel", "Error cancelling appointment", e)
                _uiState.update { it.copy(errorMessage = "Connection error while cancelling") }
            }
        }
    }

    fun logout(navigateToHome: () -> Unit) {
        NetworkClient.logout()
        navigateToHome()
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
