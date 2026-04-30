package com.example.controlh

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

actual object TokenManager {
    private const val PREFS_NAME = "jwt_prefs"
    private const val JWT_TOKEN_KEY = "jwt_token"

    @Volatile
    private var _preferences: SharedPreferences? = null

    fun init(context: Context) {
        if (_preferences == null) {
            synchronized(this) {
                if (_preferences == null) {
                    _preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    Log.d("TokenManager", "SharedPreferences initialized.")
                }
            }
        }
    }

    private val preferences: SharedPreferences
        get() = _preferences ?: throw IllegalStateException("TokenManager has not been initialized. Call init() first.")

    actual fun saveToken(token: String) {
        preferences.edit().putString(JWT_TOKEN_KEY, token).apply()
        Log.d("TokenManager", "Token saved.")
    }

    actual fun getToken(): String? {
        val token = preferences.getString(JWT_TOKEN_KEY, null)
        Log.d("TokenManager", "Token retrieved: ${if (token == null) "null" else "present"}")
        return token
    }

    actual fun clearToken() {
        preferences.edit().remove(JWT_TOKEN_KEY).apply()
        Log.d("TokenManager", "Token cleared.")
    }
}
