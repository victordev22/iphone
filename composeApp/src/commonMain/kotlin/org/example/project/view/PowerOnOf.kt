package com.example.controlh.view

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.controlh.ControlViewModel

@Composable
fun PowerOnOf(viewModel: ControlViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    val buttonColor by animateColorAsState(
        if (uiState.isPoweredOn) Color(0xFF43A047) else Color(0xFFE53935),
        label = "buttonColor"
    )

    Box(
        modifier = Modifier
            .size(120.dp)
            .shadow(
                elevation = 8.dp,
                shape = CircleShape,
                ambientColor = Color.Black.copy(alpha = 0.2f),
                spotColor = Color.Black.copy(alpha = 0.5f)
            ),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = { viewModel.togglePower() },
            modifier = Modifier.fillMaxSize(),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonColor
            ),
            enabled = !uiState.isConnecting
        ) {
            if (uiState.isConnecting) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(36.dp),
                    strokeWidth = 3.dp
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.PowerSettingsNew,
                    contentDescription = "Power",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(32.dp))
    Text(
        text = "Status: ${if (uiState.isPoweredOn) "On" else "Off"}",
        style = MaterialTheme.typography.titleLarge
    )
}
