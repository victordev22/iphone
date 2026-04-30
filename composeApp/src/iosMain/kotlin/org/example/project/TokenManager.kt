package com.example.controlh

import platform.Foundation.NSUserDefaults

actual object TokenManager {
    private val userDefaults = NSUserDefaults.standardUserDefaults
    private const val JWT_TOKEN_KEY = "jwt_token"

    actual fun saveToken(token: String) {
        userDefaults.setObject(token, JWT_TOKEN_KEY)
    }

    actual fun getToken(): String? {
        return userDefaults.stringForKey(JWT_TOKEN_KEY)
    }

    actual fun clearToken() {
        userDefaults.removeObjectForKey(JWT_TOKEN_KEY)
    }
}
