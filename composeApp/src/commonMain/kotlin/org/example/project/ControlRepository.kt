package com.example.controlh.data.repository

import com.example.controlh.data.Horas
import com.example.controlh.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ControlRepository {

    private val apiServiceHoras = RetrofitClient.instance
    private val apiService = RetrofitClient.instanceA

    suspend fun listaHoras(): Result<List<Horas>> {
        return withContext(Dispatchers.Default) {
            try {
                val response = apiServiceHoras.getHoras()
                if (response.isSuccessful) {
                    response.body()?.let {
                         Result.success(it)
                    } ?: Result.failure(Exception("Empty response body"))
                } else {
                    Result.failure(Exception("API Error: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun connectToSsh(command: String): Result<Boolean> {
        return withContext(Dispatchers.Default) {
            try {
                val response = apiService.sendCommand(command)
                if (response.isSuccessful) {
                    println("Command sent successfully: $command")
                    Result.success(true)
                } else {
                    println("Failed to send command: ${response.code()}")
                    Result.failure(Exception("Failed to send command"))
                }
            } catch (e: Exception) {
                println("Exception during SSH command: ${e.message}")
                Result.failure(e)
            }
        }
    }
}
