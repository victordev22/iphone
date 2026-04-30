package com.example.controlh

expect object TokenManager {
    fun saveToken(token: String)
    fun getToken(): String?
    fun clearToken()
}
