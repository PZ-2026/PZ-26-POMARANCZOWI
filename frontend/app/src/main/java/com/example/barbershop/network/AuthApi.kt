package com.example.barbershop.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>
}

data class LoginRequest(val email: String, val password: String)

data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String,
    val phone: String
)

data class AuthResponse(
    val token: String,
    val tokenType: String,
    val userId: Long,
    val name: String,
    val email: String,
    val phone: String,
    val role: String
)