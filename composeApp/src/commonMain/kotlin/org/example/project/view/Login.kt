package org.example.project.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding // Import for padding modifier
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController // For preview
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextField
import org.example.project.navigation.AppScreens // Assuming AppScreens is defined here



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Login(navController: NavController){
    Scaffold (
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Login") // Added "Login" text as a title
                }
            )
        }
    ) { paddingValues -> // Get paddingValues from Scaffold
        BodyContent(navController = navController, paddingValues = paddingValues)
    }
}

@Composable
fun BodyContent(navController: NavController, paddingValues: PaddingValues){ // Pass paddingValues here
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues), // Apply padding to avoid content being under TopAppBar
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("El login")
        Button(onClick = {
            navController.navigate(route = AppScreens.Home.route)
        }) {
            Text("Navega")
        }

    }
}




@Preview(showBackground = true)
@Composable
fun LoginDefaultPreview(){
    // For previewing, we need a NavController instance
    Login(navController = rememberNavController())
}

