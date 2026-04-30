package com.example.controlh.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Role(
    @SerialName("erole")
    val erole: String
)
