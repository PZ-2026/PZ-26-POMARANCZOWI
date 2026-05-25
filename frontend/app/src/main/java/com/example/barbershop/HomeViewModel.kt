package com.example.barbershop

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.barbershop.network.NetworkClient
import com.example.barbershop.network.ServiceDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val welcomeMessage: String = "Welcome to Barbershop!",
    val userName: String? = null,
    val popularServices: List<ServiceDto> = emptyList(),
    val allServices: List<ServiceDto> = emptyList(),
    val errorMessage: String? = null,
    val showNetworkErrorDialog: Boolean = false,
    val isLoggedOut: Boolean = false
)

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            NetworkClient.authState.collect { authState ->
                when (authState) {
                    is NetworkClient.AuthState.LoggedIn -> {
                        _uiState.update { it.copy(
                            userName = authState.name,
                            isLoggedOut = false
                        ) }
                    }
                    is NetworkClient.AuthState.LoggedOut -> {
                        _uiState.update { it.copy(
                            userName = null,
                            isLoggedOut = true
                        ) }
                    }
                }
            }
        }
        loadPopularServices()
        loadAllServices()
    }

    private fun loadPopularServices() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = NetworkClient.serviceApi.getPopularServices(3)
                if (response.isSuccessful) {
                    val services = response.body() ?: emptyList()
                    _uiState.update { it.copy(popularServices = services) }
                }
            } catch (_: Exception) {
                // Silently ignore - popular section will just be empty
            }
        }
    }

    private fun loadAllServices() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = NetworkClient.serviceApi.getServices()
                if (response.isSuccessful) {
                    val services = response.body() ?: emptyList()
                    _uiState.update { it.copy(allServices = services) }
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error loading all services", e)
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
    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null, showNetworkErrorDialog = false) }
    }
}
