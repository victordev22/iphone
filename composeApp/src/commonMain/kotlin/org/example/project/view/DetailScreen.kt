package com.example.project.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.project.RetrofitClient
import com.example.project.data.Horas

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DetailScreen(id: Int?, navController: NavController) {
    val service = remember { RetrofitClient.instance }

    var horaData by remember { mutableStateOf<Horas?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(id) {
        if (id != null) {
            try {
                val response = service.getHoraById(id)
                if (response.isSuccessful) {
                    horaData = response.body()
                }
            } catch (e: Exception) {
                println("Error: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard de Usuario") },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        modifier = Modifier.clickable { navController.popBackStack() },
                        tint = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Blue,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.Blue)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Text(text = "Usuario: ${horaData?.user ?: "N/A"}", style = MaterialTheme.typography.headlineSmall)
                Text(text = "ID Registro: $id", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    val estado = if (horaData?.hora_apagado == null) "Activo" else "Finalizado"
                    val colorEstado = if (horaData?.hora_apagado == null) Color(0xFF4CAF50) else Color.Gray

                    DashboardCard("Estado", estado, colorEstado)
                    DashboardCard("Inactividad", "${horaData?.minutosInactivo ?: 0} min", Color(0xFFFF9800))
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(text = "Aplicaciones en sesión:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))

                if (horaData?.listaApps.isNullOrBlank()) {
                    Text("No se registraron aplicaciones.", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                } else {
                    val appsList = horaData!!.listaApps!!
                        .split(",")
                        .map { it.trim() }
                        .filter { it.isNotEmpty() }

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        appsList.forEach { appName ->
                            AppChip(appName)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppChip(name: String) {
    androidx.compose.material3.Surface(
        color = Color.Blue.copy(alpha = 0.1f),
        shape = MaterialTheme.shapes.small,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Blue.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = Color.Blue
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = name.removeSuffix(".exe"),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Black
            )
        }
    }
}

@Composable
fun DashboardCard(label: String, value: String, color: Color) {
    androidx.compose.material3.Card(
        modifier = Modifier.width(150.dp).height(100.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, style = MaterialTheme.typography.labelMedium)
            Text(text = value, style = MaterialTheme.typography.headlineSmall, color = color)
        }
    }
}
