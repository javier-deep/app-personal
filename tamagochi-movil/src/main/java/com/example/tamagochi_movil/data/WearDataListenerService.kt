package com.example.tamagochi_movil.data

import android.provider.Settings
import android.util.Log
import com.example.tamagochi_movil.data.api.RetrofitClient
import com.example.tamagochi_movil.data.api.SensorDataRequest
import com.example.tamagochi_movil.presentation.MainViewModel
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WearDataListenerService : WearableListenerService() {

    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        for (event in dataEvents) {
            if (event.type == DataEvent.TYPE_CHANGED) {
                val path = event.dataItem.uri.path
                if (path == "/sensor_data") {
                    val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
                    val heartRate = dataMap.getFloat("heart_rate")
                    val acceleration = dataMap.getFloat("acceleration")
                    
                    Log.d("WearService", "!!! DATO RECIBIDO !!! HR=$heartRate, Accel=$acceleration")
                    
                    scope.launch(Dispatchers.Main) {
                        android.widget.Toast.makeText(applicationContext, "¡Datos del Reloj recibidos!", android.widget.Toast.LENGTH_SHORT).show()
                        MainViewModel.instance.updateData(heartRate, acceleration)
                    }
                    
                    // Enviar al Backend en Render
                    sendToBackend(heartRate, acceleration)
                }
            }
        }
    }

    private fun sendToBackend(hr: Float, acc: Float) {
        val androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        
        // Calculamos el estado rápido para el backend (igual que en el reloj)
        val linearAcc = Math.abs(acc - 9.8f)
        val state = when {
            linearAcc > 5f -> "ACTIVA"
            hr >= 100f && hr <= 180f -> "FELIZ"
            linearAcc < 1f && hr < 70f && hr > 0f -> "DORMIDA"
            else -> "TRISTE"
        }

        val request = SensorDataRequest(
            userId = androidId,
            heartRate = hr,
            acceleration = acc,
            petState = state,
            timestamp = System.currentTimeMillis()
        )

        scope.launch {
            try {
                val response = RetrofitClient.instance.sendSensorData(request)
                if (response.isSuccessful) {
                    Log.d("WearService", "¡ÉXITO! Datos guardados en MongoDB")
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("WearService", "Error del Servidor ($response.code()): $errorBody")
                }
            } catch (e: Exception) {
                Log.e("WearService", "Network error connecting to Render", e)
            }
        }
    }
}