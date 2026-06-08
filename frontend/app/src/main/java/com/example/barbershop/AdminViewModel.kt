package com.example.barbershop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.barbershop.network.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AdminUiState(
    val adminName: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class AdminViewModel(private val tokenManager: TokenManager) : ViewModel() {
    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    fun loadAdminData() {
        _uiState.value = _uiState.value.copy(
            adminName = "Administrator",
            isLoading = false
        )
    }

    fun logout() {
        viewModelScope.launch {
            tokenManager.logout()
        }
    }
}