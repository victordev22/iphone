package org.example.project

import org.example.project.data.Horas

sealed class HorasUiState {
    object Initial : HorasUiState()
    object Loading : HorasUiState()
    data class Success(val horas: List<Horas>) : HorasUiState()
    data class Error(val message: String) : HorasUiState()
}
