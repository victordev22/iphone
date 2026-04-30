package com.example.controlh.data

import com.google.gson.annotations.SerializedName

data class NovuCredentialsRequest(
    @SerializedName("providerId")
    val providerId: String,

    @SerializedName("deviceTokens")
    val deviceTokens: List<String>
)