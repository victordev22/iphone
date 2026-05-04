package org.example.project.view

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
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import org.example.project.AuthViewModel
import org.example.project.HomeViewModel
import org.example.project.HorasUiState
import org.example.project.navigation.AppScreens

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.*
import kotlin.time.Duration.Companion.milliseconds

var UserPC: String = ""


fun getSystemNow(): Instant {
    // Esta línea "engaña" al compilador de iOS al no usar el encadenamiento Clock.System
    val clockInstance: Clock = Clock.System
    return clockInstance.now()
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



fun formatMillisToTime(millis: Long): String {
    if (millis < 0) return "00:00:00"
    val duration = millis.milliseconds
    val hours = duration.inWholeHours
    val minutes = duration.inWholeMinutes % 60
    val seconds = duration.inWholeSeconds % 60
    return "${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
}

fun timeStringToMinutes(timeString: String): Int {
    val parts = timeString.split(':')
    return if (parts.size >= 2) {
        val hours = parts[0].toIntOrNull() ?: 0
        val minutes = parts[1].toIntOrNull() ?: 0
        (hours * 60) + minutes
    } else {
        0
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val isLoading by authViewModel.isLoading.collectAsState()

    val horasUiState by homeViewModel.horasUiState.collectAsState()
    val horasData = if (horasUiState is HorasUiState.Success) (horasUiState as HorasUiState.Success).horas else emptyList()
    val horasLoading = horasUiState is HorasUiState.Loading
    val horasError = if (horasUiState is HorasUiState.Error) (horasUiState as HorasUiState.Error).message else null

    val userHoursToday by homeViewModel.currentUserHours.collectAsState()
    val weeklyUsageSummary by homeViewModel.weeklyUsageSummary.collectAsState()

    LaunchedEffect(currentUser, isAuthenticated, isLoading) {
        if (!isAuthenticated) {
            navController.navigate(AppScreens.Auth.route) { popUpTo(AppScreens.Home.route) { inclusive = true } }
            return@LaunchedEffect
        }
        if (isAuthenticated && currentUser == null && !isLoading) {
            authViewModel.handleInvalidUserSession(navController)
            return@LaunchedEffect
        }

        if (currentUser != null) {
            val currentUserId = currentUser!!.nickname
            homeViewModel.fetchHoras(currentUserId)
        }
    }

    val usuarioActual = currentUser?.nickname
    val datosus: String = usuarioActual ?: "Cargando..."

    if (currentUser == null || (horasLoading && horasData.isEmpty())) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
            Text(if (isLoading) "Cargando perfil..." else "Cargando horas...", modifier = Modifier.padding(top = 80.dp))
        }
        return
    }

    val currentUserId = currentUser!!.nickname
    UserPC = currentUserId.takeLast(2).uppercase()

    val usageTimeDisplay = remember(userHoursToday) {
        if (userHoursToday != null) {
            val startInstant = parseDateTime(userHoursToday!!.hora_encendido)
            if (startInstant != null) {
                val endInstant = parseDateTime(userHoursToday!!.hora_apagado) ?: getSystemNow()
                val durationMillis = endInstant.toEpochMilliseconds() - startInstant.toEpochMilliseconds()
                formatMillisToTime(durationMillis)
            } else "00:00:00"
        } else {
            "00:00:00"
        }
    }
    
    val elapsedDailyMinutes = remember(usageTimeDisplay) {
        timeStringToMinutes(usageTimeDisplay)
    }

    val totalWeeklyMillis = remember(weeklyUsageSummary) {
        weeklyUsageSummary.values.sum()
    }
    
    val totalWeeklyMinutes = remember(totalWeeklyMillis) {
        (totalWeeklyMillis / (1000 * 60)).toInt()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = datosus) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Blue, titleContentColor = Color.White)
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController, authViewModel = authViewModel)
        }
    ) { paddingValues ->
        val isAdmin = currentUser!!.roles.any {it.erole == "ROLE_ADMIN"}

        if(horasError != null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("Error de carga: $horasError", color = Color.Red)
            }
        } else if(isAdmin){
            AdminBodyContent(
                navController = navController,
                paddingValues = paddingValues,
                elapsedDailyMinutes = elapsedDailyMinutes,
                totalWeeklyMinutes = totalWeeklyMinutes,
                homeViewModel = homeViewModel
            )
        }else{
            HomeBodyContent(
                navController = navController,
                paddingValues = paddingValues,
                elapsedDailyMinutes = elapsedDailyMinutes,
                totalWeeklyMinutes = totalWeeklyMinutes,
                homeViewModel = homeViewModel,
                usageTimeDisplay = usageTimeDisplay
            )
        }
    }
}


@Composable
fun AdminBodyContent(
    navController: NavController,
    paddingValues: PaddingValues,
    elapsedDailyMinutes: Int,
    totalWeeklyMinutes: Int,
    homeViewModel: HomeViewModel,
) {
    val dailyMaxMinutes = 480 // 8 hours
    val weeklyMaxMinutes = 2400 // 40 hours

    Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {
        Text(text = "Panel de Administrador", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            UsageLimitProgress(usedMinutes = elapsedDailyMinutes, maxMinutes = dailyMaxMinutes, label = "Diario", canvasSize = 150.dp)
            UsageLimitProgress(usedMinutes = totalWeeklyMinutes, maxMinutes = weeklyMaxMinutes, label = "Semanal", canvasSize = 150.dp)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { navController.navigate(route = AppScreens.List.route) }
            ) {
                Text("Ver Listado de Horas")
            }
            Spacer(modifier = Modifier.height(16.dp))
            PowerOnOf()
        }
    }
}

@Composable
fun HomeBodyContent(
    navController: NavController,
    paddingValues: PaddingValues,
    elapsedDailyMinutes: Int,
    totalWeeklyMinutes: Int,
    homeViewModel: HomeViewModel,
    usageTimeDisplay: String
){
    val dailyMaxMinutes = 480
    val weeklyMaxMinutes = 2400

    Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
        Column(
            modifier = Modifier.weight(1f).fillMaxWidth().padding(top = 32.dp, start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Control de PC Personal", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tu PC asignada es PC$UserPC | Hoy: $usageTimeDisplay",
                fontSize = 16.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                UsageLimitProgress(usedMinutes = elapsedDailyMinutes, maxMinutes = dailyMaxMinutes, label = "Diario", canvasSize = 150.dp)
                UsageLimitProgress(usedMinutes = totalWeeklyMinutes, maxMinutes = weeklyMaxMinutes, label = "Semanal", canvasSize = 150.dp)
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PowerOnOf()
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController, authViewModel: AuthViewModel) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val userData by authViewModel.userData.collectAsState()
    val isAdmin = userData?.roles?.any { it.erole == "ROLE_ADMIN" } ?: false

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {
        NavigationBarItem(
            selected = currentRoute == AppScreens.Home.route,
            onClick = {
                navController.navigate(AppScreens.Home.route) {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") }
        )

        NavigationBarItem(
            selected = currentRoute == AppScreens.List.route,
            onClick = {
                navController.navigate(AppScreens.List.route) {
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = { Icon(Icons.Default.List, contentDescription = "Horas") },
            label = { Text("Horas") }
        )

        if (isAdmin) {
            NavigationBarItem(
                selected = currentRoute == AppScreens.ListU.route,
                onClick = {
                    navController.navigate(AppScreens.ListU.route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(Icons.Default.Person, contentDescription = "Administración") },
                label = { Text("Usuarios") }
            )
        }

        NavigationBarItem(
            selected = false,
            onClick = { authViewModel.logout(navController) },
            icon = { Icon(Icons.Default.ExitToApp, contentDescription = "Salir") },
            label = { Text("Salir") }
        )
    }
}

@Composable
fun UsageLimitProgress(usedMinutes: Int, maxMinutes: Int, label: String, canvasSize: androidx.compose.ui.unit.Dp) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CircularProgressIndicator(
            progress = (usedMinutes.toFloat() / maxMinutes.toFloat()).coerceIn(0f, 1f),
            modifier = Modifier.size(canvasSize)
        )
        Text(text = label)
        Text(text = "$usedMinutes/$maxMinutes min")
    }
}
