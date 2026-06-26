package com.example.tamagochi_movil

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.tamagochi_movil.presentation.MainViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var tvHeartRate: TextView
    private lateinit var tvAcceleration: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvHeartRate = findViewById(R.id.tvHeartRate)
        tvAcceleration = findViewById(R.id.tvAcceleration)

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            MainViewModel.instance.heartRate.collect { hr ->
                tvHeartRate.text = "Frecuencia Cardiaca: ${String.format("%.1f", hr)} bpm"
            }
        }

        lifecycleScope.launch {
            MainViewModel.instance.acceleration.collect { acc ->
                tvAcceleration.text = "Aceleración: ${String.format("%.1f", acc)} m/s²"
            }
        }
    }
}