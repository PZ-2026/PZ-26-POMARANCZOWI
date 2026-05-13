package com.example.barbershop

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// mock użytkownika
data class HomeUiState(
    val welcomeMessage: String = "Witaj w BarberShop!",
    val userName: String = "Jan Kowalski",
    val userEmail: String = "jan.kowalski@test.com",
    val userPhone: String = "+48 123 456 789",
    val userRole: String = "CLIENT"
)

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // dodac fetchuserdata
}