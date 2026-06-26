package com.example.tamagochi_fit.data

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.sqrt

class PetSensorManager(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private val _heartRate = MutableStateFlow(0f)
    val heartRate: StateFlow<Float> = _heartRate

    private val _acceleration = MutableStateFlow(0f)
    val acceleration: StateFlow<Float> = _acceleration

    private val dataClient = Wearable.getDataClient(context)
    private val scope = CoroutineScope(Dispatchers.IO)

    fun startListening() {
        Log.d("PetSensorManager", "Starting listeners...")
        heartRateSensor?.let {
            val registered = sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
            Log.d("PetSensorManager", "Heart rate sensor registered: $registered")
        } ?: Log.w("PetSensorManager", "Heart rate sensor not found")

        accelerometer?.let {
            val registered = sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
            Log.d("PetSensorManager", "Accelerometer registered: $registered")
        } ?: Log.w("PetSensorManager", "Accelerometer not found")
    }

    fun stopListening() {
        Log.d("PetSensorManager", "Stopping listeners")
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_HEART_RATE -> {
                val hr = event.values[0]
                Log.d("PetSensorManager", "HR changed: $hr")
                _heartRate.value = hr
                sendDataToMobile("heart_rate", hr)
            }
            Sensor.TYPE_ACCELEROMETER -> {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                val magnitude = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
                Log.d("PetSensorManager", "Accel changed: $magnitude")
                _acceleration.value = magnitude
                sendDataToMobile("acceleration", magnitude)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun sendDataToMobile(key: String, value: Float) {
        scope.launch {
            try {
                val putDataMapReq = PutDataMapRequest.create("/sensor_data").apply {
                    dataMap.putFloat(key, value)
                    dataMap.putLong("timestamp", System.currentTimeMillis())
                }
                val putDataReq = putDataMapReq.asPutDataRequest()
                putDataReq.setUrgent()
                dataClient.putDataItem(putDataReq)
            } catch (e: Exception) {
                Log.e("PetSensorManager", "Error sending data to mobile", e)
            }
        }
    }
}