package com.example.controlh.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterCenterFocus
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.controlh.RetrofitClient
import com.example.controlh.data.Horas
import com.example.controlh.navigation.AppScreens
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.*
import kotlin.time.Duration.Companion.hours

enum class FilterType {
    ALL,
    POWERED_ON,
    CAME_LATE,
    MENOS_TIME,
    BY_USER
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(navController: NavController){
    val service = remember { RetrofitClient.instance }

    val horasList = remember { mutableStateListOf<Horas>() }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var filterType by remember { mutableStateOf(FilterType.ALL) }
    var showMenu by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf<String?>(null) }
    val uniqueUsers = remember { mutableStateListOf<String>() }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch(Dispatchers.Default) {
            try {
                isLoading = true
                errorMessage = null
                val response = service.getHoras()
                if (response.isSuccessful) {
                    val fetchedHoras = response.body()
                    if (fetchedHoras != null) {
                        withContext(Dispatchers.Main) {
                            horasList.clear()
                            horasList.addAll(fetchedHoras)
                            uniqueUsers.clear()
                            uniqueUsers.addAll(fetchedHoras.map { it.user }.distinct().sorted())
                        }
                    } else {
                        errorMessage = "No data received."
                    }
                } else {
                    errorMessage = "Error: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = "Network error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    val filteredHoras = remember(horasList, filterType, selectedUser) {
        val listToFilter = if (selectedUser != null) {
            horasList.filter { it.user == selectedUser }
        } else {
            horasList
        }

        val tz = TimeZone.currentSystemDefault()

        when (filterType) {
            FilterType.ALL, FilterType.BY_USER -> listToFilter
            FilterType.POWERED_ON -> listToFilter.filter { it.hora_apagado == null }
            FilterType.CAME_LATE -> listToFilter.filter {
                val startInstant = parseDateTime(it.hora_encendido)
                if (startInstant != null) {
                    val localTime = startInstant.toLocalDateTime(tz).time
                    localTime.hour >= 9 && localTime.minute > 0
                } else false
            }
            FilterType.MENOS_TIME -> listToFilter.filter {
                val startInstant = parseDateTime(it.hora_encendido)
                val endInstant = parseDateTime(it.hora_apagado)
                if (startInstant != null && endInstant != null) {
                    val duration = endInstant - startInstant
                    duration < 8.hours
                } else false
            }
        }
    }

    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text(text = "List") },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Arrow back",
                        modifier = Modifier.clickable { navController.popBackStack() }
                    )
                },
                actions = {
                    Box {
                        Icon(
                            imageVector = Icons.Default.FilterCenterFocus,
                            contentDescription = "Menu",
                            modifier = Modifier
                                .clickable { showMenu = true }
                                .padding(end = 16.dp)
                        )
                        DropdownFilterMenu(
                            expanded = showMenu,
                            uniqueUsers = uniqueUsers,
                            onDismissRequest = { showMenu = false },
                            onFilterSelected = { newFilter, user ->
                                filterType = newFilter
                                selectedUser = user
                                showMenu = false
                            },
                            selectedUser = selectedUser
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Blue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        ListBodyContent(
            navController = navController,
            paddingValues = paddingValues,
            horasList = filteredHoras,
            isLoading = isLoading,
            errorMessage = errorMessage,
            onRetry = {
                coroutineScope.launch(Dispatchers.Default) {
                    try {
                        isLoading = true
                        errorMessage = null
                        val response = service.getHoras()
                        if (response.isSuccessful) {
                            val fetchedHoras = response.body()
                            if (fetchedHoras != null) {
                                withContext(Dispatchers.Main) {
                                    horasList.clear()
                                    horasList.addAll(fetchedHoras)
                                }
                            } else {
                                errorMessage = "No data received on retry."
                            }
                        } else {
                            errorMessage = "Retry failed: ${response.code()}"
                        }
                    } catch (e: Exception) {
                        errorMessage = "Retry network error: ${e.message}"
                    } finally {
                        isLoading = false
                    }
                }
            }
        )
    }
}

private fun parseDateTime(dateTimeString: String?): Instant? {
    if (dateTimeString == null) return null
    return try {
        Instant.parse(dateTimeString)
    } catch (e: Exception) {
        try {
            Instant.parse(dateTimeString.replace(" ", "T") + "Z")
        } catch (e2: Exception) {
            null
        }
    }
}

@Composable
fun DropdownFilterMenu(
    expanded: Boolean,
    uniqueUsers: List<String>,
    onDismissRequest: () -> Unit,
    onFilterSelected: (FilterType, String?) -> Unit,
    selectedUser: String?
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest
    ) {
        DropdownMenuItem(
            text = { Text("Ver todos") },
            onClick = { onFilterSelected(FilterType.ALL, null) }
        )
        DropdownMenuItem(
            text = { Text("PCs Encendidas") },
            onClick = { onFilterSelected(FilterType.POWERED_ON, null) }
        )
        DropdownMenuItem(
            text = { Text("Entraron Tarde") },
            onClick = { onFilterSelected(FilterType.CAME_LATE, null) }
        )
        DropdownMenuItem(
            text = { Text("Menos Horas") },
            onClick = { onFilterSelected(FilterType.MENOS_TIME, null) }
        )
        DropdownMenuItem(
            text = { Text("--- Filtrar por Usuario ---") },
            onClick = { },
            enabled = false
        )
        uniqueUsers.forEach { user ->
            DropdownMenuItem(
                text = { Text(user) },
                onClick = { onFilterSelected(FilterType.BY_USER, user) },
                trailingIcon = {
                    if (user == selectedUser) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected"
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun ListBodyContent(
    navController: NavController,
    paddingValues: PaddingValues,
    horasList: List<Horas>,
    isLoading: Boolean,
    errorMessage: String?,
    onRetry: () -> Unit
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Cargando datos...", style = MaterialTheme.typography.bodyLarge)
            }
        } else if (errorMessage != null) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Error: $errorMessage", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onRetry) {
                    Text("Reintentar")
                }
            }
        } else {
            if (horasList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay registros de horas.", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LasHoras(horas = horasList) { horaSeleccionada ->
                    navController.navigate("detail_screen/${horaSeleccionada.id}")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            navController.navigate(route = AppScreens.Home.route)
        }) {
            Text("Ir a Home")
        }
    }
}

@Composable
fun LasHoras(horas: List<Horas>, onHoraClick: (Horas) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 8.dp)
    ) {
        items(horas) { hora ->
            MyComponent(hora, onHoraClick)
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun MyComponent(hor: Horas, onHoraClick: (Horas) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        CardItemComposable(hor = hor, onClick = { onHoraClick(hor) })
    }
}

@Composable
fun CardItemComposable(hor: Horas, onClick: () -> Unit) {
    androidx.compose.material3.Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium,
        elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = hor.user,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Encendido: ${hor.hora_encendido}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Apagado: ${hor.hora_apagado ?: "---"}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Inactividad: ${hor.minutosInactivo} min",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
