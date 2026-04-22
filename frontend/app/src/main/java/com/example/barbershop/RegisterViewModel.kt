package com.example.barbershop

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val password: String = "",
    val repeatPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class RegisterViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onPhoneChange(phone: String) {
        _uiState.update { it.copy(phone = phone) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun onRepeatPasswordChange(repeatPassword: String) {
        _uiState.update { it.copy(repeatPassword = repeatPassword) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun register() {
        val currentState = _uiState.value

        // simple validation
        if (currentState.password != currentState.repeatPassword) {
            _uiState.update { it.copy(errorMessage = "Passwords do not match!") }
            return
        }

        _uiState.update { it.copy(errorMessage = null, isLoading = true) }

    }
}