package com.example.controlh.view


import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text

import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.controlh.AuthViewModel
import com.example.controlh.navigation.AppScreens
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(navController: NavController, viewModel: AuthViewModel = viewModel()) {
    val isAuthenticated by viewModel.isAuthenticated.collectAsState()

    // Observe authentication state and navigate if authenticated
    LaunchedEffect(isAuthenticated) {
        if (isAuthenticated) {
            navController.navigate(AppScreens.Home.route) {
                popUpTo(AppScreens.Auth.route) { inclusive = true } // Clear auth screen from back stack
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Authentication")
                }
            )
        }
    ) { paddingValues ->
        AuthBodyContent(navController = navController, viewModel = viewModel, paddingValues = paddingValues)
    }
}