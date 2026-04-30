package org.example.project

import org.example.project.service.ApiService
import org.example.project.data.*

object RetrofitClient {
    private val apiService = ApiService()

    // Usamos lazy para asegurar que se cree correctamente en el hilo principal de ser necesario
    val instance by lazy { KtorBridge(apiService) }
    val instanceA by lazy { KtorBridge(apiService) }

    class KtorBridge(private val api: ApiService) {
        suspend fun login(request: LoginRequest) = wrap { api.login(request) }
        suspend fun signup(request: SignupRequest) = wrap { api.signup(request) }
        suspend fun getCurrentUser() = wrap { api.getCurrentUser() }
        suspend fun getRawCurrentUserJson() = wrap { api.getRawCurrentUserJson() }
        suspend fun getProtectedResource() = wrap { api.getProtectedResource() }
        suspend fun getHoras() = wrap { api.getHoras() }
        suspend fun getHoraById(id: Int) = wrap { api.getHoraById(id) }
        suspend fun sendCommand(command: String) = wrap { api.sendCommand(command) }
        suspend fun getAllUsersFull() = wrap { api.getAllUsersFull() }
        suspend fun updateUser(id: Int, user: UserFull) = wrap { api.updateUser(id, user) }
        suspend fun updateUserRole(email: String, roleRequest: RoleUpdateRequest) = wrap { api.updateUserRole(email, roleRequest) }
        suspend fun deleteUser(id: Long) = wrap { api.deleteUser(id) }

        // El truco está en marcar T como Any para ayudar al compilador de iOS
        private suspend fun <T : Any> wrap(call: suspend () -> T): KtorResponse<T> {
            return try {
                val result = call()
                KtorResponse(isSuccessful = true, body = result)
            } catch (e: Exception) {
                // En iOS, los mensajes de error de red pueden ser nulos, usamos un fallback
                KtorResponse(isSuccessful = false, body = null, errorBodyString = e.message ?: "Unknown Error", code = 500)
            }
        }
    }
}

// Clase de respuesta que imita el comportamiento de Retrofit.Response
class KtorResponse<T>(
    val isSuccessful: Boolean,
    private val body: T?,
    private val errorBodyString: String? = null,
    private val code: Int = 0
) {
    fun body(): T? = body
    fun errorBody() = errorBodyString?.let { KtorErrorBody(it) }
    fun code() = code
}

class KtorErrorBody(private val content: String) {
    fun string() = content
}