package com.example.project.data

import kotlinx.serialization.Serializable

@Serializable
data class SignupRequest(
    val nickname: String,
    val email: String,
    val password: String
)
