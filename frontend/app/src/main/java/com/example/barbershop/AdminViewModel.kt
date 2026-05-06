package com.example.barbershop

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AdminUiState(
    val adminName: String = "",
    val isLoading: Boolean = false
)

class AdminViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    fun loadAdminData() {
        // TODO: Pobierz dane admina z backendu
        _uiState.value = _uiState.value.copy(isLoading = true)
    }

    fun logout() {
        // TODO
    }
}