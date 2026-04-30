package org.example.project

expect object TokenManager {
    fun saveToken(token: String)
    fun getToken(): String?
    fun clearToken()
}
