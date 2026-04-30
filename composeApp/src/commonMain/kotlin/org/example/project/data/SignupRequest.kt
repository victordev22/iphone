package com.example.controlh.data

import kotlinx.serialization.Serializable

@Serializable
data class SignupRequest(
    val nickname: String,
    val email: String,
    val password: String
)
