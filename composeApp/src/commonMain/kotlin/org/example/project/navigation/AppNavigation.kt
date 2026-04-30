package com.example.controlh.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.controlh.view.HomeScreen
import com.example.controlh.view.Login
import com.example.controlh.view.SplashScreen
import com.example.controlh.view.AuthScreen
import com.example.controlh.view.DetailScreen
import com.example.controlh.view.ListScreen
import com.example.controlh.view.ListUser

@Composable
fun AppNavigation(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AppScreens.SplashScreen.route){
        composable(route = AppScreens.Login.route) {
            Login(navController)
        }
        composable(route = AppScreens.Home.route) {
            HomeScreen(navController)
        }
        composable(route = AppScreens.List.route) {
            ListScreen(navController)
        }
        composable(route = AppScreens.SplashScreen.route) {
            SplashScreen(navController)
        }

        composable(route = AppScreens.Auth.route) {
            AuthScreen(navController)
        }

        composable(route = AppScreens.ListU.route) {
            ListUser(navController)
        }

        // NUEVA RUTA DE DETALLE
        composable(
            route = AppScreens.Detail.route,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            DetailScreen(id = id, navController = navController) // Asegúrate de que DetailScreen reciba Int? o Int
        }

    }

}