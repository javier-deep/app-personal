package com.example.tamagochi_movil.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface SensorApiService {
    @POST("api/v1/sensor-data")
    suspend fun sendSensorData(@Body data: SensorDataRequest): Response<Unit>

    @GET("api/v1/sensor-data/{userId}")
    suspend fun getHistory(@Path("userId") userId: String): Response<List<SensorDataRequest>>
}
