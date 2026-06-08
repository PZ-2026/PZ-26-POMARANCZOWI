package com.example.barbershop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.barbershop.network.NetworkClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ManageUsersUiState(
    val users: List<UserDto> = emptyList(),
    val isLoading: Boolean = false
)

class ManageUsersViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ManageUsersUiState())
    val uiState: StateFlow<ManageUsersUiState> = _uiState.asStateFlow()

    init {
        loadUsers()
    }

    private fun loadUsers() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val response = NetworkClient.userApi.getUsers()
                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(
                        users = response.body() ?: emptyList(),
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun deleteUser(userId: Long) {
        viewModelScope.launch {
            try {
                val response = NetworkClient.userApi.deleteUser(userId)
                if (response.isSuccessful) {
                    loadUsers()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun saveUser(user: UserDto) {
        viewModelScope.launch {
            try {
                if (user.userId == null) {
                    NetworkClient.userApi.createUser(user)
                } else {
                    NetworkClient.userApi.updateUser(user.userId, user)
                }
                loadUsers()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}