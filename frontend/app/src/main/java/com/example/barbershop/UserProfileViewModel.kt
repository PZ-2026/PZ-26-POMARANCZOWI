package com.example.barbershop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.barbershop.network.NetworkClient
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
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class UserProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UserProfileUiState())
    val uiState: StateFlow<UserProfileUiState> = _uiState.asStateFlow()

    init {
        // Observe global auth state
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
                    }
                    is NetworkClient.AuthState.LoggedOut -> {
                        // Reset state on logout with a 0.75 second delay
                        delay(750);
                        _uiState.update { UserProfileUiState() }
                    }
                }
            }
        }
    }

    fun logout(navigateToHome: () -> Unit) {
        NetworkClient.logout()
        navigateToHome()
    }
}
