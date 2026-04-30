package com.example.controlh.service

import com.example.controlh.Constants
import com.example.controlh.KtorClient
import com.example.controlh.data.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class ApiService {
    private val client = KtorClient.client

    // Auth methods
    suspend fun login(request: LoginRequest): JwtResponse {
        return client.post("${Constants.BASE_AUTH}/auth/signin") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun signup(request: SignupRequest): String {
        return client.post("${Constants.BASE_AUTH}/auth/signup") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.bodyAsText()
    }

    suspend fun getCurrentUser(): User {
        return client.get("${Constants.BASE_AUTH}/auth/me").body()
    }

    suspend fun getRawCurrentUserJson(): UserFull {
        return client.get("${Constants.BASE_AUTH}/auth/me").body()
    }

    // Control methods
    suspend fun getHoras(): List<Horas> {
        return client.get("${Constants.BASE_URL}control/listhoras").body()
    }

    suspend fun getHoraById(id: Int): Horas {
        return client.get("${Constants.BASE_URL}control/list/$id").body()
    }

    suspend fun sendCommand(command: String): String {
        return client.get("${Constants.BASE_AUTH}api/ssh/execute") {
            parameter("command", command)
        }.bodyAsText()
    }

    // User Management
    suspend fun getAllUsersFull(): List<UserFull> {
        return client.get("${Constants.BASE_AUTH}api/users").body() // Verify this endpoint
    }

    suspend fun updateUser(id: Int, user: UserFull): MessageResponse {
        return client.put("${Constants.BASE_AUTH}/update/$id") {
            contentType(ContentType.Application.Json)
            setBody(user)
        }.body()
    }

    suspend fun updateUserRole(email: String, roleRequest: RoleUpdateRequest): MessageResponse {
        return client.put("${Constants.BASE_AUTH}/update/role/$email") {
            contentType(ContentType.Application.Json)
            setBody(roleRequest)
        }.body()
    }

    suspend fun deleteUser(id: Long) {
        client.delete("${Constants.BASE_AUTH}update/delete/$id")
    }
}
