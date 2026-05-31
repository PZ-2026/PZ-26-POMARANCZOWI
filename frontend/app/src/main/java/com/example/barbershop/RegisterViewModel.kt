package com.example.barbershop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.barbershop.network.NetworkClient
import com.example.barbershop.network.RegisterRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

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

    fun register(onSuccess: () -> Unit) {
        val currentState = _uiState.value

        // Validation
        if (currentState.name.isBlank() || currentState.email.isBlank() || 
            currentState.phone.isBlank() || currentState.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "All fields are required") }
            return
        }

        if (currentState.password.length < 6) {
            _uiState.update { it.copy(errorMessage = "Password must be at least 6 characters") }
            return
        }

        if (currentState.password != currentState.repeatPassword) {
            _uiState.update { it.copy(errorMessage = "Passwords do not match!") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(errorMessage = null, isLoading = true) }

            try {
                val response = withContext(Dispatchers.IO) {
                    NetworkClient.authApi.register(
                        RegisterRequest(
                            name = currentState.name,
                            email = currentState.email,
                            phone = currentState.phone,
                            password = currentState.password
                        )
                    )
                }

                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    
                    // Automatically log the user in
                    NetworkClient.saveAuthResponse(authResponse)
                    
                    _uiState.update { it.copy(isLoading = false) }
                    onSuccess()
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMsg = try {
                        if (errorBody != null && errorBody.startsWith("{")) {
                            JSONObject(errorBody).optString("message", "Registration failed")
                        } else {
                            errorBody ?: "Registration failed"
                        }
                    } catch (e: Exception) {
                        "Registration failed: ${response.code()}"
                    }
                    _uiState.update { it.copy(isLoading = false, errorMessage = errorMsg) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Connection error: ${e.message}") }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
