package com.example.controlh.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class NovuCredentialsWrapper(
    @SerialName("credentials")
    val credentials: NovuCredentialsRequest
)
