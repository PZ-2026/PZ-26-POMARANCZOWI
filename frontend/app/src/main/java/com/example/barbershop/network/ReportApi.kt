package com.example.barbershop.network

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ReportApi {
    @GET("/reports/barber/{barberId}")
    suspend fun getBarberStatistics(@Path("barberId") barberId: Long): Response<ResponseBody>

    @GET("/reports/revenue")
    suspend fun getRevenue(@Query("period") period: String = "month"): Response<ResponseBody>

    @GET("/reports/services-popularity")
    suspend fun getServicePopularity(): Response<ResponseBody>
}