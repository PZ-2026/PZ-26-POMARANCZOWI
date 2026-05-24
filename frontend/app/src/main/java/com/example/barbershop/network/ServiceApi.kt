package com.example.barbershop.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ServiceApi {
    @GET("api/services")
    suspend fun getServices(): Response<List<ServiceDto>>

    @GET("api/services/popular")
    suspend fun getPopularServices(@Query("limit") limit: Int = 3): Response<List<ServiceDto>>

    @GET("api/services/{id}")
    suspend fun getServiceById(@Path("id") id: Long): Response<ServiceDto>
}