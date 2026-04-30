package com.example.barbershop.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ServiceApi {
    @GET("api/services")
    suspend fun getServices(): Response<List<ServiceDto>>

    @GET("api/services/{id}")
    suspend fun getServiceById(@Path("id") id: Long): Response<ServiceDto>
}