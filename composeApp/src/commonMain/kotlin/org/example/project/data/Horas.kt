package com.example.controlh.data

import kotlinx.serialization.Serializable

@Serializable
data class Horas(
    val id: Int,
    val user: String,
    val hora_encendido: String, // Changed from Date to String
    val hora_apagado: String? = null, // Changed from Date to String?
    val minutosInactivo: Int,
    val listaApps: String? = null
)
