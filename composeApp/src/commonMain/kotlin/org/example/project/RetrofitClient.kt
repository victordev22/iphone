package com.example.controlh

import com.example.controlh.service.ApiService
import com.example.controlh.data.*
import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*

/**
 * A bridge to keep existing code working while using Ktor under the hood.
 */
object RetrofitClient {
    private val apiService = ApiService()

    val instance = KtorBridge(apiService)
    val instanceA = KtorBridge(apiService)

    class KtorBridge(private val api: ApiService) {
        suspend fun login(request: LoginRequest) = wrap { api.login(request) }
        suspend fun signup(request: SignupRequest) = wrap { api.signup(request) }
        suspend fun getCurrentUser() = wrap { api.getCurrentUser() }
        suspend fun getRawCurrentUserJson() = wrap { api.getRawCurrentUserJson() }
        suspend fun getHoras() = wrap { api.getHoras() }
        suspend fun getHoraById(id: Int) = wrap { api.getHoraById(id) }
        suspend fun sendCommand(command: String) = wrap { api.sendCommand(command) }
        suspend fun getAllUsersFull() = wrap { api.getAllUsersFull() }
        suspend fun updateUser(id: Int, user: UserFull) = wrap { api.updateUser(id, user) }
        suspend fun updateUserRole(email: String, roleRequest: RoleUpdateRequest) = wrap { api.updateUserRole(email, roleRequest) }
        suspend fun deleteUser(id: Long) = wrap { api.deleteUser(id) }
        
        private suspend fun <T> wrap(call: suspend () -> T): KtorResponse<T> {
            return try {
                val result = call()
                KtorResponse(true, result)
            } catch (e: Exception) {
                KtorResponse(false, null, e.message, 500)
            }
        }
    }
}

class KtorResponse<T>(
    val isSuccessful: Boolean,
    private val body: T?,
    private val errorBodyString: String? = null,
    private val code: Int = 0
) {
    fun body() = body
    fun errorBody() = errorBodyString?.let { KtorErrorBody(it) }
    fun code() = code
}

class KtorErrorBody(private val content: String) {
    fun string() = content
}
