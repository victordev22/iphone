package com.example.controlh

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.controlh.data.repository.ControlRepository
import com.example.controlh.view.UserPC
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PowerUiState(
    val isPoweredOn: Boolean = false,
    val isConnecting: Boolean = false,
    val errorMessage: String? = null
)

class ControlViewModel(
    private val controlRepository: ControlRepository = ControlRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(PowerUiState())
    val uiState: StateFlow<PowerUiState> = _uiState.asStateFlow()

    init {
        fetchInitialPowerState()
    }

    fun togglePower() {
        viewModelScope.launch {
            _uiState.update { it.copy(isConnecting = true, errorMessage = null) }

            val command = if (_uiState.value.isPoweredOn) "sh " + UserPC + "_off.sh" else "sh " + UserPC + ".sh"
            
            val response = try {
                RetrofitClient.instanceA.sendCommand(command)
            } catch (e: Exception) {
                _uiState.update { it.copy(isConnecting = false, errorMessage = "Network error: ${e.message}") }
                return@launch
            }

            if (response.isSuccessful) {
                _uiState.update {
                    it.copy(
                        isPoweredOn = !it.isPoweredOn,
                        isConnecting = false
                    )
                }
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                _uiState.update {
                    it.copy(
                        isConnecting = false,
                        errorMessage = "Failed to toggle power: $errorMessage (Code: ${response.code()})"
                    )
                }
            }
        }
    }

    private fun fetchInitialPowerState() {
        // Temporary: Should get current user from AuthViewModel instead of MyApp
        viewModelScope.launch {
            _uiState.update { it.copy(isConnecting = true) }
            val result = controlRepository.listaHoras()

            if (result.isSuccess) {
                // For now, we'll just check if there's any active PC since we don't have MyApp.currentUser here anymore
                val isPCFound = result.getOrNull()?.any { it.hora_apagado == null } ?: false
                println("PC Status Check: isPCFound=$isPCFound")
                _uiState.update { it.copy(isPoweredOn = isPCFound, isConnecting = false) }
            } else {
                _uiState.update {
                    it.copy(
                        isConnecting = false,
                        errorMessage = "Failed to fetch status: ${result.exceptionOrNull()?.message}"
                    )
                }
            }
        }
    }
}
