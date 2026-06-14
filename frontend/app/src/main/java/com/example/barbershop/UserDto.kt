package com.example.barbershop

data class UserDto(
    val userId: Long? = null,
    val name: String,
    val email: String,
    val phone: String,
    val role: String,
    val password: String? = null

)