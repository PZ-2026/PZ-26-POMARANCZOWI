package com.example.barbershop

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class EmployeeAppointment(
    val id: String,
    val clientName: String,
    val date: String,
    val time: String,
    val service: String,
    val status: String
)

data class EmployeeUiState(
    val employeeName: String = "",
    val appointments: List<EmployeeAppointment> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class EmployeeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(EmployeeUiState())
    val uiState: StateFlow<EmployeeUiState> = _uiState.asStateFlow()

    // Funkcja inicjalizująca
    fun loadEmployeeData() {
        // TODO
        _uiState.value = _uiState.value.copy(isLoading = true)
    }

    fun addAppointment(appointment: EmployeeAppointment) {
        // TODO: Wyślij zapytanie POST do API
        val currentList = _uiState.value.appointments.toMutableList()
        currentList.add(appointment)
        _uiState.value = _uiState.value.copy(appointments = currentList)
    }

    fun editAppointment(appointment: EmployeeAppointment) {
        // TODO: Wyślij zapytanie PUT do API
        val currentList = _uiState.value.appointments.map {
            if (it.id == appointment.id) appointment else it
        }
        _uiState.value = _uiState.value.copy(appointments = currentList)
    }

    fun deleteAppointment(appointmentId: String) {
        // TODO: Wyślij zapytanie DELETE do API
        val currentList = _uiState.value.appointments.filter { it.id != appointmentId }
        _uiState.value = _uiState.value.copy(appointments = currentList)
    }

    fun markAppointmentAsCompleted(appointmentId: String) {
        // TODO: Wyślij zapytanie PUT/PATCH
        val currentList = _uiState.value.appointments.map {
            if (it.id == appointmentId) it.copy(status = "Completed") else it
        }
        _uiState.value = _uiState.value.copy(appointments = currentList)
    }

    fun logout() {
        // TODO
    }
}