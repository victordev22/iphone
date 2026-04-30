package org.example.project

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import com.example.controlh.TokenManager
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging

class MyApp : Application() {

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "notification_fcm"
    }

    override fun onCreate() {
        super.onCreate()
        // Initialize TokenManager (assuming it will be moved or available)
        try {
            TokenManager.init(this)
        } catch (e: Exception) {
            Log.e("MyApp", "Error initializing TokenManager: ${e.message}")
        }

        Firebase.messaging.token.addOnCompleteListener {
            if (!it.isSuccessful) {
                println("El token no sirve")
                return@addOnCompleteListener
            }
            val token = it.result
            println("el token es> $token")
        }
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Notificationes de FCM",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Estas notificaciones van a ser recibidas desde FCM"
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}
