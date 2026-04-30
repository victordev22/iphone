package org.example.project

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.work.*
import com.example.controlh.Constants
import com.example.controlh.RetrofitClient
import com.example.controlh.service.AuthService
import com.example.controlh.service.ControlService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalTime
import java.util.concurrent.TimeUnit

class NotificationWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    private val authService: AuthService by lazy { RetrofitClient.instanceA }
    private val service: ControlService by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ControlService::class.java)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        val now = LocalTime.now()
        Log.d("NotificationWorker", "TRABAJO INICIADO A LAS: $now")

        return try {
            val userResponse = authService.getRawCurrentUserJson()
            if (!userResponse.isSuccessful) return Result.retry()

            val user = userResponse.body() ?: return Result.failure()
            val targetTime = LocalTime.parse(user.of_control)
            val myUser = user.nickname
            val currentLocalTime = LocalTime.now()

            var isPcStillOn = false

            val response = service.getHoras()
            if (response.isSuccessful) {
                val fetchedHoras = response.body()
                if (fetchedHoras != null && currentLocalTime.isAfter(targetTime)) {
                    isPcStillOn = fetchedHoras.any { it.user == myUser && it.hora_apagado == null }
                }
            }

            if (isPcStillOn) {
                Log.d("NotificationWorker", "PC encendida: Enviando aviso.")
                // TODO: Implement notification logic for Android
            }

            reprogramarSiguienteEjecucion(targetTime, isPcStillOn)

            Result.success()
        } catch (e: Exception) {
            Log.e("NotificationWorker", "Error: ${e.message}")
            Result.retry()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun reprogramarSiguienteEjecucion(targetTime: LocalTime, insistirEn15Min: Boolean) {
        val delayInSeconds: Long
        val now = LocalTime.now()

        if (insistirEn15Min) {
            delayInSeconds = 15 * 60
        } else {
            var duration = java.time.Duration.between(now, targetTime).seconds
            if (duration <= 0) duration += 24 * 3600
            delayInSeconds = duration
        }

        val nextWork = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(delayInSeconds, TimeUnit.SECONDS)
            .addTag("DailyCheck")
            .setConstraints(Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build())
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniqueWork(
            "DailyCheck",
            ExistingWorkPolicy.REPLACE,
            nextWork
        )
    }
}
