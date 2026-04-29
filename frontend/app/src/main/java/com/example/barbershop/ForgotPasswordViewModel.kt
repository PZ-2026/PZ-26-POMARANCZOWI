package com.example.barbershop

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ForgotPasswordUiState(
    val email: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

class ForgotPasswordViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, errorMessage = null, isSuccess = false) }
    }

    fun sendResetLink() {
        val email = _uiState.value.email
        if (email.isNotBlank()) {
            _uiState.update { it.copy(isLoading = true) }

            // TODO: here sending do Spring
            // simulation
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isSuccess = true,
                    errorMessage = null
                )
            }
        } else {
            _uiState.update { it.copy(errorMessage = "Email cannot be empty") }
        }
    }
}