package com.example.barbershop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.barbershop.network.NetworkClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val welcomeMessage: String = "Welcome to Barbershop!",
    val userName: String? = null,
    val errorMessage: String? = null,
    val showNetworkErrorDialog: Boolean = false,
    val isLoggedOut: Boolean = false
)

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        fetchUserInfo()
    }

    fun fetchUserInfo() {
        if (!NetworkClient.isLoggedIn()) {
            _uiState.update { it.copy(userName = null) }
            return
        }

        viewModelScope.launch {
            try {
                val response = NetworkClient.authApi.getMe()
                if (response.isSuccessful && response.body() != null) {
                    _uiState.update { it.copy(userName = response.body()?.name) }
                } else if (response.code() == 401 || response.code() == 403) {
                    logoutUser()
                } else {
                    _uiState.update { it.copy(errorMessage = "Error: ${response.code()}") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(showNetworkErrorDialog = true) }
            }
        }
    }

    private fun logoutUser() {
        NetworkClient.logout()
        _uiState.update { it.copy(userName = null, isLoggedOut = true) }
    }

    fun onProfileClick(navigateToLogin: () -> Unit, navigateToProfile: () -> Unit) {
        if (!NetworkClient.isLoggedIn()) {
            navigateToLogin()
            return
        }

        viewModelScope.launch {
            try {
                val response = NetworkClient.authApi.getMe()
                if (response.isSuccessful) {
                    navigateToProfile()
                } else {
                    logoutUser()
                    navigateToLogin()
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(showNetworkErrorDialog = true) }
            }
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null, showNetworkErrorDialog = false) }
    }
}
