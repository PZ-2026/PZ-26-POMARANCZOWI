package com.example.barbershop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.barbershop.network.AuthResponse
import com.example.barbershop.network.NetworkClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val isDarkTheme: Boolean = false,
    val isLoadingMe: Boolean = false,
    val meData: AuthResponse? = null,
    val meError: String? = null
)

class SettingsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun toggleTheme(isDark: Boolean) {
        _uiState.update { it.copy(isDarkTheme = isDark) }
    }

    fun getMe() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoadingMe = true, meError = null, meData = null) }
            try {
                val response = NetworkClient.authApi.getMe()
                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    _uiState.update {
                        it.copy(isLoadingMe = false, meData = authResponse)
                    }
                } else {
                    _uiState.update {
                        it.copy(isLoadingMe = false, meError = "Failed to fetch profile: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoadingMe = false, meError = "Connection error: ${e.message}")
                }
            }
        }
    }

    fun onProfileClick(navigateToLogin: () -> Unit, navigateToProfile: () -> Unit, navigateToEmployeePanel : () -> Unit, navigateToAdminPanel : () -> Unit) {
        if (!NetworkClient.isLoggedIn()) {
            navigateToLogin()
        } else {
            viewModelScope.launch {
                NetworkClient.authState.collect {
                    // If role is ADMIN, navigate to AdminProfile
                    if (it is NetworkClient.AuthState.LoggedIn && it.role == "ADMIN") {
                        navigateToAdminPanel()
                    }
                    // If role is EMPLOYEE, navigate to BarberProfile
                    else if (it is NetworkClient.AuthState.LoggedIn && it.role == "EMPLOYEE") {
                        navigateToEmployeePanel()
                    }
                    // else navigate to UserProfile
                    else {
                        navigateToProfile()
                    }
                }
            }
        }
    }
}
