package org.example.project.data

import kotlinx.serialization.Serializable

@Serializable
data class UserFull(
    val id: Int,
    val nickname: String,
    val email: String,
    val password: String,
    val on_control: String,
    val of_control: String,
    val roles: List<Role>
)
