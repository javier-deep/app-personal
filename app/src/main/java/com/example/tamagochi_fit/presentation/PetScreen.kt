package com.example.tamagochi_fit.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import com.example.tamagochi_fit.domain.PetState

@Composable
fun PetScreen(viewModel: PetViewModel = viewModel()) {
    val petState by viewModel.petState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PetView(petState)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = petState.name,
            style = MaterialTheme.typography.titleMedium,
            color = getStatusColor(petState)
        )
    }
}

@Composable
fun PetView(state: PetState) {
    val emoji = when (state) {
        PetState.ACTIVA -> "🏃"
        PetState.FELIZ -> "😊"
        PetState.TRISTE -> "😔"
        PetState.DORMIDA -> "😴"
    }
    Text(text = emoji, fontSize = 64.sp)
}

fun getStatusColor(state: PetState): Color {
    return when (state) {
        PetState.ACTIVA -> Color.Green
        PetState.FELIZ -> Color.Yellow
        PetState.TRISTE -> Color.Gray
        PetState.DORMIDA -> Color.Blue
    }
}