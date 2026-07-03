package com.example.tamagochi_movil.data.api

data class SensorDataRequest(
    val userId: String,
    val heartRate: Float,
    val acceleration: Float,
    val petState: String,
    val timestamp: Long
)