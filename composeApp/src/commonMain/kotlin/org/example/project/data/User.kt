package com.example.project.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class User(
    @SerialName("nickname") val nickname: String,
    @SerialName("email") val email: String,
    @SerialName("roles") val roles: List<Role>
)
