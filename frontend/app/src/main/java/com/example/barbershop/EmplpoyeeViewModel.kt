package com.example.barbershop

import android.content.Context
import android.os.Environment
import android.util.Log
import android.widget.Toast
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
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

data class EmployeeUiState(
    val employeeName: String = "",
    val email: String = "",
    val phone: String = "",
    val appointments: List<AppointmentResponse> = emptyList(),
    val barberId: Long? = null,
    val isBarberIdLoaded: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

class EmployeeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(EmployeeUiState())
    val uiState: StateFlow<EmployeeUiState> = _uiState.asStateFlow()

    private val _isReportLoading = MutableStateFlow(false)
    val isReportLoading: StateFlow<Boolean> = _isReportLoading.asStateFlow()

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
                        resolveBarberId(authState.email)
                    }
                    is NetworkClient.AuthState.LoggedOut -> {
                        delay(750)
                        _uiState.update { EmployeeUiState() }
                    }
                }
            }
        }
    }

    private fun resolveBarberId(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = NetworkClient.barberApi.getBarbers()
                if (response.isSuccessful) {
                    val barbers = response.body() ?: emptyList()
                    val myBarberProfile = barbers.find { it.email.equals(email, ignoreCase = true) }

                    _uiState.update {
                        it.copy(
                            barberId = myBarberProfile?.barberId,
                            isBarberIdLoaded = true
                        )
                    }
                } else {
                    _uiState.update { it.copy(isBarberIdLoaded = true) }
                }
            } catch (e: Exception) {
                Log.e("EmployeeViewModel", "Error resolving barber ID", e)
                _uiState.update { it.copy(isBarberIdLoaded = true) }
            }
        }
    }

    fun loadEmployeeData(barberUserId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val response = NetworkClient.appointmentApi.getBarberAppointments()
                if (response.isSuccessful) {
                    val appointments = response.body() ?: emptyList()
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
                val response = NetworkClient.appointmentApi.updateAppointmentStatus(appointmentId, "COMPLETED")
                if (response.isSuccessful) {
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

    fun downloadMyStatistics(context: Context, barberId: Long) {
        viewModelScope.launch {
            _isReportLoading.value = true
            try {
                val response = withContext(Dispatchers.IO) {
                    NetworkClient.reportApi.getBarberStatistics(barberId)
                }

                if (response.isSuccessful && response.body() != null) {
                    val bytes = response.body()!!.bytes()
                    savePdf(context, bytes, "My_Statistics_Barber_${barberId}.pdf")
                } else {
                    Toast.makeText(context, "Error downloading report (code ${response.code()})", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                _isReportLoading.value = false
            }
        }
    }

    private fun savePdf(context: Context, bytes: ByteArray, fileName: String) {
        try {
            val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            val file = File(dir, fileName)
            FileOutputStream(file).use { it.write(bytes) }
            Toast.makeText(context, "Statistics downloaded to:\n${file.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Error saving PDF file", Toast.LENGTH_SHORT).show()
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