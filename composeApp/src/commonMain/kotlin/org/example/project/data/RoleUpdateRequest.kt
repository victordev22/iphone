package org.example.project.data

import kotlinx.serialization.Serializable

@Serializable
data class RoleUpdateRequest(val newRoleId: Int)
