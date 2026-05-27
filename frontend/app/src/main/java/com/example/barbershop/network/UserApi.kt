package com.example.barbershop.network

import com.example.barbershop.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserApi {
    @GET("api/users")
    suspend fun getUsers(): Response<List<UserDto>>

    @GET("api/users/{id}")
    suspend fun getUserById(@Path("id") id: Long): Response<UserDto>

    @POST("api/users")
    suspend fun createUser(@Body user: UserDto): Response<UserDto>

    @PUT("api/users/{id}")
    suspend fun updateUser(@Path("id") id: Long, @Body user: UserDto): Response<UserDto>

    @DELETE("api/users/{id}")
    suspend fun deleteUser(@Path("id") id: Long): Response<Unit>
}