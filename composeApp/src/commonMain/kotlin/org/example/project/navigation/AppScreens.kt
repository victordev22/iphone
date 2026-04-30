package com.example.controlh.navigation

sealed class AppScreens (val route: String){
    object Login:AppScreens("login")
    object Home:AppScreens("home")
    object List:AppScreens("list")
    object ListU:AppScreens("listu")
    object SplashScreen:AppScreens("splashscreen")
    object Auth:AppScreens("auth")


    object Detail:AppScreens("detail_screen/{id}") {
        fun createRoute(id: Int) = "detail_screen/$id"
    }

}