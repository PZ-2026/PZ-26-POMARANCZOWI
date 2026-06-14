package com.example.barbershop.network

import retrofit2.Response
import retrofit2.http.*

interface AvailabilityApi {

    @GET("api/availability/{barberId}")
    suspend fun getAvailabilityByBarber(@Path("barberId") barberId: Long): Response<List<AvailabilityDto>>

    @POST("api/availability")
    suspend fun createAvailability(@Body availability: AvailabilityDto): Response<AvailabilityDto>

    @PUT("api/availability/{id}")
    suspend fun updateAvailability(
        @Path("id") id: Long,
        @Body availability: AvailabilityDto
    ): Response<AvailabilityDto>

    @DELETE("api/availability/{id}")
    suspend fun deleteAvailability(@Path("id") id: Long): Response<Unit>

    @GET("api/availability/barber/{barberId}/date/{date}/available-times")
    suspend fun getAvailableTimes(
        @Path("barberId") barberId: Long,
        @Path("date") date: String,
        @Query("serviceDuration") serviceDuration: String = "PT30M"
    ): Response<List<String>>
}