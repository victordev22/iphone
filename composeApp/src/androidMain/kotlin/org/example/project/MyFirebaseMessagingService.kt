package org.example.project.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import androidx.core.app.NotificationCompat
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        var title = remoteMessage.notification?.title
        var body = remoteMessage.notification?.body

        if (remoteMessage.data.isNotEmpty()) {
            if (title == null) title = "Alerta de Control H"
            if (body == null) {
                val pcName = remoteMessage.data["computerName"] ?: "PC Desconocida"
                body = "La computadora $pcName sigue encendida fuera de horario."
            }
        }

        if (title != null && body != null) {
            enviarNotificacionVisual(title, body)
        }
    }

    private fun enviarNotificacionVisual(title: String, messageBody: String) {
        val channelId = "control_h_alerts"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Alertas de Equipos",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Canal para alertas de computadoras encendidas"
                enableLights(true)
                lightColor = android.graphics.Color.RED
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(this, channelId)
            // .setSmallIcon(R.drawable.logo) // Removed to avoid R issues for now, or use android.R.drawable.ic_dialog_info
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000, 1000, 1000))

        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("Novu", "Nuevo token generado: $token")
    }
}
