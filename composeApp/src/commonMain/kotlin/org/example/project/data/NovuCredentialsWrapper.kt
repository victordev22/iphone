package com.example.controlh.data

import com.google.gson.annotations.SerializedName

data class NovuCredentialsWrapper(
    @SerializedName("credentials")
    val credentials: NovuCredentialsRequest
)