package org.example.project.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.example.project.view.HomeScreen
import org.example.project.view.Login
import org.example.project.view.SplashScreen
import org.example.project.view.AuthScreen
import org.example.project.view.DetailScreen
import org.example.project.view.ListScreen
import org.example.project.view.ListUser

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

        composable(
            route = AppScreens.Detail.route,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            DetailScreen(id = id, navController = navController)
        }
    }
}
