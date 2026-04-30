package com.example.controlh

import com.example.controlh.service.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TurnOn {
    private val servicio: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.BASE_ON)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

//    suspend fun fetchDataForId(number: Int) {
//        withContext(Dispatchers.IO) {
//            try {
//                val response = servicio.getData(number)
//
//                if (response.isSuccessful) {
//                    val data = response.body()
//                    println("Datos recibidos para ID $number: $data")
//                    // Procesa tus datos aquí
//                } else {
//                    println("Error al obtener datos para ID $number: Código ${response.code()} - Mensaje: ${response.message()}")
//                    // Maneja el error
//                }
//            } catch (e: Exception) {
//                println("Excepción al conectar: ${e.localizedMessage}")
//                // Maneja la excepción de red
//            }
//        }
//    }

}