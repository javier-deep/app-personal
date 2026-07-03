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
import kotlinx.coroutines.launch
import kotlin.math.sqrt

class PetSensorManager(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private var lastHeartRate = 0f
    private var lastAcceleration = 0f

    private val _heartRate = kotlinx.coroutines.flow.MutableStateFlow(0f)
    val heartRate = _heartRate
    private val _acceleration = kotlinx.coroutines.flow.MutableStateFlow(0f)
    val acceleration = _acceleration

    private val dataClient = Wearable.getDataClient(context)
    private val scope = CoroutineScope(Dispatchers.IO)

    fun startListening() {
        heartRateSensor?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
        accelerometer?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
    }

    fun stopListening() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_HEART_RATE -> {
                lastHeartRate = event.values[0]
                _heartRate.value = lastHeartRate
                syncData()
            }
            Sensor.TYPE_ACCELEROMETER -> {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                lastAcceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
                _acceleration.value = lastAcceleration
                syncData()
            }
        }
    }

    private fun syncData() {
        scope.launch {
            try {
                val putDataMapReq = PutDataMapRequest.create("/sensor_data").apply {
                    dataMap.putFloat("heart_rate", lastHeartRate)
                    dataMap.putFloat("acceleration", lastAcceleration)
                    dataMap.putLong("timestamp", System.currentTimeMillis())
                }
                dataClient.putDataItem(putDataMapReq.asPutDataRequest().setUrgent())
                Log.d("PetSensorManager", "Sent to Mobile: HR=$lastHeartRate, Acc=$lastAcceleration")
            } catch (e: Exception) {
                Log.e("PetSensorManager", "Sync failed", e)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}