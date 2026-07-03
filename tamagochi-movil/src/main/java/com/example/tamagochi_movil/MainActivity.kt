package com.example.tamagochi_movil

import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.tamagochi_movil.presentation.MainViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var tvHeartRate: TextView
    private lateinit var tvAcceleration: TextView
    private lateinit var tvHistory: TextView
    private lateinit var btnRefresh: Button

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
        tvHistory = findViewById(R.id.tvHistory)
        btnRefresh = findViewById(R.id.btnRefresh)

        val androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

        btnRefresh.setOnClickListener {
            MainViewModel.instance.fetchHistory(androidId)
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            MainViewModel.instance.heartRate.collect { hr ->
                tvHeartRate.text = "${String.format("%.1f", hr)} bpm"
            }
        }

        lifecycleScope.launch {
            MainViewModel.instance.acceleration.collect { acc ->
                tvAcceleration.text = "${String.format("%.1f", acc)} m/s²"
            }
        }

        lifecycleScope.launch {
            MainViewModel.instance.history.collect { history ->
                if (history.isEmpty()) {
                    tvHistory.text = "No hay datos en la base de datos aún."
                } else {
                    val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                    val historyText = history.takeLast(10).reversed().joinToString("\n\n") { data ->
                        val date = sdf.format(Date(data.timestamp))
                        "[$date] HR: ${data.heartRate.toInt()} bpm | ACC: ${String.format("%.1f", data.acceleration)} | STATE: ${data.petState}"
                    }
                    tvHistory.text = historyText
                }
            }
        }
    }
}
