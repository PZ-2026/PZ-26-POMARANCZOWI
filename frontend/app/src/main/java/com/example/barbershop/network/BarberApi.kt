package com.example.barbershop.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BarberApi {
    @GET("api/barbers")
    suspend fun getBarbers(): Response<List<BarberDto>>

    @GET("api/barbers/{id}")
    suspend fun getBarberById(@Path("id") id: Long): Response<BarberDto>

    @GET("api/availability/{barberId}")
    suspend fun getAvailability(@Path("barberId") barberId: Long): Response<List<AvailabilityDto>>

    @GET("api/appointments/barber/{barberId}/busy-times")
    suspend fun getBusyTimes(
        @Path("barberId") barberId: Long,
        @Query("date") date: String
    ): Response<List<AppointmentResponse>>
}

data class BarberDto(
    val barberId: Long,
    val name: String,
    val email: String,
    val phone: String,
    val specialization: String?,
    val bio: String?,
    val role: String
)

data class AvailabilityDto(
    val availabilityId: Long,
    val dayOfWeek: Int,
    val startTime: String,
    val endTime: String
)

data class AppointmentResponse(
    val appointmentId: Long,
    val barber: BarberDto,
    val startTime: String,
    val endTime: String,
    val status: String,
    val createdAt: String,
    val services: List<ServiceDto>
)

data class ServiceDto(
    val serviceId: Long,
    val name: String,
    val description: String?,
    val durationMinutes: Int,
    val price: Double,
    val isActive: Boolean
)