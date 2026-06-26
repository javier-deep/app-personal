package com.example.tamagochi_movil.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel : ViewModel() {
    private val _heartRate = MutableStateFlow(0f)
    val heartRate: StateFlow<Float> = _heartRate

    private val _acceleration = MutableStateFlow(0f)
    val acceleration: StateFlow<Float> = _acceleration

    fun updateData(hr: Float, acc: Float) {
        _heartRate.value = hr
        _acceleration.value = acc
    }

    companion object {
        // Singleton simple para propósitos de demostración
        val instance = MainViewModel()
    }
}