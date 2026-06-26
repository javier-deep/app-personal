package com.example.tamagochi_fit.presentation

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tamagochi_fit.data.PetSensorManager
import com.example.tamagochi_fit.domain.PetState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class PetViewModel(application: Application) : AndroidViewModel(application) {

    private val sensorManager = PetSensorManager(application)
    
    private val _petState = MutableStateFlow(PetState.DORMIDA)
    val petState: StateFlow<PetState> = _petState

    init {
        Log.d("PetViewModel", "Initializing PetViewModel")
        observeSensors()
    }

    fun startSensors() {
        Log.d("PetViewModel", "Starting sensors from ViewModel")
        sensorManager.startListening()
    }

    private fun observeSensors() {
        viewModelScope.launch {
            combine(sensorManager.heartRate, sensorManager.acceleration) { heartRate, acceleration ->
                calculatePetState(heartRate, acceleration)
            }.collect { newState ->
                Log.d("PetViewModel", "New Pet State: $newState")
                _petState.value = newState
            }
        }
    }

    private fun calculatePetState(heartRate: Float, acceleration: Float): PetState {
        // En el emulador, la gravedad es ~9.8 m/s². 
        // Restamos la gravedad aproximada para detectar movimiento real.
        val linearAcceleration = Math.abs(acceleration - 9.8f)

        return when {
            linearAcceleration > 5f -> PetState.ACTIVA
            (heartRate >= 100f && heartRate <= 180f) -> PetState.FELIZ
            (linearAcceleration < 1f && heartRate < 70f && heartRate > 0f) -> PetState.DORMIDA
            linearAcceleration < 1f -> PetState.TRISTE
            else -> PetState.ACTIVA
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("PetViewModel", "Clearing ViewModel, stopping sensors")
        sensorManager.stopListening()
    }
}