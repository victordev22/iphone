package com.example.project

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object NovuManager {
    private val client = KtorClient.client

    fun vincularDispositivo(emailUsuario: String, fcmToken: String) {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val cleanToken = fcmToken.replace("Token de Firebase obtenido: ", "").trim()

                val jsonParams = """
                    {
                        "providerId": "fcm",
                        "integrationIdentifier": "fcm",
                        "credentials": {
                            "deviceTokens": ["$cleanToken"]
                        }
                    }
                """.trimIndent()

                val response = client.put("http://4.245.229.134:3000/v1/subscribers/$emailUsuario/credentials") {
                    header("Authorization", "ApiKey a9470a49ff639faee7192102182ba376")
                    contentType(ContentType.Application.Json)
                    setBody(jsonParams)
                }

                if (response.status.isSuccess()) {
                    println("✅ Vinculado con éxito al email: $emailUsuario")
                } else {
                    println("❌ Error del servidor (${response.status.value}): ${response.bodyAsText()}")
                }
            } catch (e: Exception) {
                println("❌ Excepción vinculando Novu: ${e.message}")
            }
        }
    }
}
