package com.example.barbershop

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ManageUsersUiState(
    val users: List<UserDto> = emptyList()
)

class ManageUsersViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ManageUsersUiState())
    val uiState: StateFlow<ManageUsersUiState> = _uiState.asStateFlow()

    init {
        // mock danych
        _uiState.value = ManageUsersUiState(
            users = listOf(
                UserDto(1L, "Jan Kowalski", "jan.kowalski@example.com", "123456789", "CUSTOMER"),
                UserDto(2L, "Anna Nowak", "anna.nowak@example.com", "987654321", "BARBER"),
                UserDto(3L, "Michał Admin", "admin@example.com", "555666777", "ADMIN")
            )
        )
    }

    fun deleteUser(userId: Long) {
        val updatedList = _uiState.value.users.filterNot { it.userId == userId }
        _uiState.value = _uiState.value.copy(users = updatedList)
    }

    fun saveUser(user: UserDto) {
        val currentList = _uiState.value.users.toMutableList()

        if (user.userId == null) {
            // Dodawanie nowego użytkownika
            val newId = (currentList.maxOfOrNull { it.userId ?: 0L } ?: 0L) + 1L
            currentList.add(user.copy(userId = newId))
        } else {
            // Edycja istniejącego
            val index = currentList.indexOfFirst { it.userId == user.userId }
            if (index != -1) {
                currentList[index] = user
            }
        }

        _uiState.value = _uiState.value.copy(users = currentList)
    }
}