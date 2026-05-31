package com.example.barbershop

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.barbershop.network.AppointmentResponse
import com.example.barbershop.network.NetworkClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EmployeeUiState(
    val employeeName: String = "",
    val email: String = "",
    val phone: String = "",
    val appointments: List<AppointmentResponse> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class EmployeeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(EmployeeUiState())
    val uiState: StateFlow<EmployeeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            NetworkClient.authState.collect { authState ->
                when (authState) {
                    is NetworkClient.AuthState.LoggedIn -> {
                        _uiState.update {
                            it.copy(
                                employeeName = authState.name,
                                email = authState.email,
                                phone = authState.phone,
                                isLoading = false
                            )
                        }
                        loadEmployeeData(authState.userId)
                    }
                    is NetworkClient.AuthState.LoggedOut -> {
                        delay(750)
                        _uiState.update { EmployeeUiState() }
                    }
                }
            }
        }
    }

    fun loadEmployeeData(barberUserId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // Fetch appointments for barber
                val response = NetworkClient.appointmentApi.getBarberAppointments()
                if (response.isSuccessful) {
                    val appointments = response.body() ?: emptyList()
                    // Order by appointment time
                    val sorted = appointments.sortedBy { it.startTime }
                    _uiState.update { it.copy(appointments = sorted, isLoading = false) }
                } else {
                    Log.e("EmployeeViewModel", "Failed to load: ${response.code()}")
                    _uiState.update { it.copy(isLoading = false, error = "Failed to load schedule") }
                }
            } catch (e: Exception) {
                Log.e("EmployeeViewModel", "Connection error", e)
                _uiState.update { it.copy(isLoading = false, error = "Connection error") }
            }
        }
    }

    fun markAppointmentAsCompleted(appointmentId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Assuming "COMPLETED" is the status name
                val response = NetworkClient.appointmentApi.updateAppointmentStatus(appointmentId, "COMPLETED")
                if (response.isSuccessful) {
                    // Refresh data
                    val auth = NetworkClient.authState.value
                    if (auth is NetworkClient.AuthState.LoggedIn) {
                        loadEmployeeData(auth.userId)
                    }
                }
            } catch (e: Exception) {
                Log.e("EmployeeViewModel", "Error updating status", e)
            }
        }
    }

    fun logout(navigateToHome: () -> Unit) {
        NetworkClient.logout()
        navigateToHome()
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
