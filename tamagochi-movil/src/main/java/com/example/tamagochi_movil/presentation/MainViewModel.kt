package com.example.tamagochi_movil.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tamagochi_movil.data.api.RetrofitClient
import com.example.tamagochi_movil.data.api.SensorDataRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _heartRate = MutableStateFlow(0f)
    val heartRate: StateFlow<Float> = _heartRate

    private val _acceleration = MutableStateFlow(0f)
    val acceleration: StateFlow<Float> = _acceleration

    private val _history = MutableStateFlow<List<SensorDataRequest>>(emptyList())
    val history: StateFlow<List<SensorDataRequest>> = _history

    fun updateData(hr: Float, acc: Float) {
        _heartRate.value = hr
        _acceleration.value = acc
    }

    fun fetchHistory(userId: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.getHistory(userId)
                if (response.isSuccessful) {
                    _history.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                // Manejar error de conexión
            }
        }
    }

    companion object {
        // Singleton simple para que el Service pueda acceder fácilmente
        val instance = MainViewModel()
    }
}
