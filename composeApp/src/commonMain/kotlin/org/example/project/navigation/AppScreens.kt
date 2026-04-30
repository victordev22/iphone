package org.example.project.navigation

sealed class AppScreens(val route: String) {
    object SplashScreen: AppScreens("splash_screen")
    object Login: AppScreens("login_screen")
    object Home: AppScreens("home_screen")
    object List: AppScreens("list_screen")
    object Auth: AppScreens("auth_screen")
    object ListU: AppScreens("list_user")
    object Detail: AppScreens("detail_screen/{id}")
}
