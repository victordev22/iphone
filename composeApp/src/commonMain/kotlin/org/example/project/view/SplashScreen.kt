// File: com/example/controlh/view/SplashScreen.kt
package org.example.project.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import org.example.project.AuthViewModel
import org.example.project.navigation.AppScreens
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    // Collect the authentication state as a Composable state
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()

    // This block will execute once when the Composable is first launched.
    // It will also re-execute if the 'isAuthenticated' state changes.
    LaunchedEffect(key1 = isAuthenticated) {
        // We add a small delay to ensure the splash screen is visible for a moment.
        delay(3000)

        // If the user is authenticated (a token exists), navigate to the Home screen.
        if (isAuthenticated) {
            navController.navigate(AppScreens.Home.route) {
                // This ensures the user cannot go back to the splash screen.
                popUpTo(AppScreens.SplashScreen.route) { inclusive = true }
            }
        } else {
            // If not authenticated, navigate to the Auth (login/signup) screen.
            navController.navigate(AppScreens.Auth.route) {
                popUpTo(AppScreens.SplashScreen.route) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Welcome",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview(){
    SplashScreen(navController = rememberNavController())
}
