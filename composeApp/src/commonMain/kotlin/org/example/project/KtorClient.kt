package com.example.project

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object KtorClient {
    val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
        
        install(Logging) {
            level = LogLevel.INFO
            logger = Logger.DEFAULT
        }
        
        install(Auth) {
            bearer {
                loadTokens {
                    TokenManager.getToken()?.let { BearerTokens(it, "") }
                }
            }
        }
        
        defaultRequest {
            // We can set default headers here if needed
        }
    }
}
