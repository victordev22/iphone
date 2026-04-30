package org.example.project.data

import kotlinx.serialization.Serializable

@Serializable
data class JwtResponse(
    val token: String,
    val email: String,
    val nickname: String,
    val roles: Set<String>
)
