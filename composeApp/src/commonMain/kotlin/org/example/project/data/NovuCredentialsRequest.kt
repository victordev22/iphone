package com.example.project.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class NovuCredentialsRequest(
    @SerialName("providerId")
    val providerId: String,

    @SerialName("deviceTokens")
    val deviceTokens: List<String>
)
