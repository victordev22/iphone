package org.example.project

import android.content.Context
import android.content.SharedPreferences

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
                }
            }
        }
    }

    private val preferences: SharedPreferences
        get() = _preferences ?: throw IllegalStateException("TokenManager has not been initialized. Call init() first.")

    actual fun saveToken(token: String) {
        preferences.edit().putString(JWT_TOKEN_KEY, token).apply()
    }

    actual fun getToken(): String? {
        return preferences.getString(JWT_TOKEN_KEY, null)
    }

    actual fun clearToken() {
        preferences.edit().remove(JWT_TOKEN_KEY).apply()
    }
}
