package com.example.tamagochi_fit.presentation

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.wear.compose.material3.AppScaffold
import com.example.tamagochi_fit.presentation.theme.TamagochifitTheme

class MainActivity : ComponentActivity() {
    private val viewModel: PetViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val permissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { result ->
                if (result.values.all { it }) {
                    viewModel.startSensors()
                }
            }

            LaunchedEffect(Unit) {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.BODY_SENSORS,
                        Manifest.permission.ACTIVITY_RECOGNITION
                    )
                )
            }

            TamagochifitTheme {
                AppScaffold {
                    PetScreen(viewModel)
                }
            }
        }
    }
}