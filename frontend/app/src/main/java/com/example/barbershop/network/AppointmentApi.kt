package com.example.barbershop.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface AppointmentApi {
    @GET("api/appointments")
    suspend fun getMyAppointments(): Response<List<AppointmentResponse>>

    @POST("api/appointments")
    suspend fun createAppointment(@Body request: AppointmentRequest): Response<AppointmentResponse>

    @GET("api/appointments/barber")
    suspend fun getBarberAppointments(): Response<List<AppointmentResponse>>

    @PUT("api/appointments/{id}/status")
    suspend fun updateAppointmentStatus(
        @Path("id") id: Long,
        @Query("status") status: String
    ): Response<AppointmentResponse>
}

data class AppointmentRequest(
    val barberId: Long,
    val serviceIds: List<Long>,
    val startTime: String
)