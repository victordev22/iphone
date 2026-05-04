package org.example.project

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import org.example.project.data.LoginRequest
import org.example.project.data.SignupRequest
import org.example.project.data.User
import org.example.project.navigation.AppScreens
import org.example.project.data.Role
import org.example.project.data.UserFull
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel() : ViewModel() {
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _nickname = MutableStateFlow("")
    val nickname: StateFlow<String> = _nickname

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

    private val _isLoginScreen = MutableStateFlow(true)
    val isLoginScreen: StateFlow<Boolean> = _isLoginScreen

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated

    private val _protectedResourceData = MutableStateFlow<String?>(null)
    val protectedResourceData: StateFlow<String?> = _protectedResourceData

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val _userData = MutableStateFlow<UserFull?>(null)
    val userData: StateFlow<UserFull?> = _userData

    init {
        _isAuthenticated.value = TokenManager.getToken() != null
        println("AuthViewModel initialized. isAuthenticated: ${_isAuthenticated.value}")

        if (_isAuthenticated.value) {
            fetchCurrentUser()
        }
    }

    fun onEmailChange(newEmail: String) { _email.value = newEmail }
    fun onPasswordChange(newPassword: String) { _password.value = newPassword }
    fun onNicknameChange(newNickname: String) { _nickname.value = newNickname }

    fun toggleAuthScreen() {
        _isLoginScreen.value = !_isLoginScreen.value
        _errorMessage.value = null
        _successMessage.value = null
        _email.value = ""
        _password.value = ""
        _nickname.value = ""
    }

    fun signIn(navController: NavController) {
        _isLoading.value = true
        _errorMessage.value = null
        _successMessage.value = null
        viewModelScope.launch {
            try {
                val request = LoginRequest(email.value, password.value)
                val response = RetrofitClient.instance.login(request)
                if (response.isSuccessful) {
                    val jwtResponse = response.body()
                    jwtResponse?.let {
                        TokenManager.saveToken(it.token)
                        _isAuthenticated.value = true
                        _successMessage.value = "${it.email} signed in successfully!"
                        println("Login Success. Token saved.")
                        fetchCurrentUser()
                    } ?: run {
                        _errorMessage.value = "Login successful, but no JWT received."
                    }
                } else {
                    _errorMessage.value = "Error de inicio de sesión (${response.code()})": "Login failed!"
                    println("Login Error: ${response.code()}")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Network error: ${e.message}"
                println("Login Exception: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signUp(navController: NavController) {
        _isLoading.value = true
        _errorMessage.value = null
        _successMessage.value = null
        viewModelScope.launch {
            try {
                val request = SignupRequest(nickname.value, email.value, password.value)
                val response = RetrofitClient.instanceA.signup(request)

                if (response.isSuccessful) {
                    val rawMessage = "${response.code()}: "Signup successful!"
                    _successMessage.value = rawMessage
                    println("Signup Success: $rawMessage")
                    _isLoginScreen.value = true
                } else {
                    val errorMsg = "Error ${response.code()}: "Signup failed!"
                    _errorMessage.value = errorMsg
                    println("Signup Error: $errorMsg")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Network error: ${e.message}"
                println("Signup Exception: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getProtectedData() {
        _isLoading.value = true
        _protectedResourceData.value = null
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.getProtectedResource()
                if (response.isSuccessful) {
                    _protectedResourceData.value = response.body() ?: "Protected resource data received."
                    _successMessage.value = "Protected data fetched successfully!"
                } else {
                    _errorMessage.value = response.errorBody()?.string() ?: "Failed to fetch protected data."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Network error fetching protected data: ${e.message}"
                println("Protected Data Exception: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchCurrentUser() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.instanceA.getCurrentUser()
                val responsa = RetrofitClient.instanceA.getRawCurrentUserJson()

                if (responsa.isSuccessful) {
                    val fetchedUsera = responsa.body()
                    _userData.value = fetchedUsera
                    println("Successfully fetched user content : ${fetchedUsera}")
                } else {
                    _errorMessage.value = "Failed to fetch user data: ${responsa.code()}"
                    println("Failed to fetch user data: ${responsa.errorBody()?.string()}")
                }


                if (response.isSuccessful) {
                    val fetchedUser = response.body()
                    _currentUser.value = fetchedUser
                    println("Successfully fetched user: ${_currentUser.value?.email}")
                } else {
                    _errorMessage.value = "Failed to fetch user data: ${response.code()}"
                    println("Failed to fetch user data: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Exception fetching user: ${e.message}"
                println("Exception while fetching user: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun handleInvalidUserSession(navController: NavController) {
        viewModelScope.launch {
            TokenManager.clearToken()
            _isAuthenticated.value = false
            _successMessage.value = "Session expired. Please log in again."
            _protectedResourceData.value = null
            _currentUser.value = null
            _userData.value = null
            _email.value = ""
            _password.value = ""
            _nickname.value = ""
            _isLoginScreen.value = true
            println("Invalid session handled. Token cleared.")

            navController.navigate(AppScreens.Auth.route) {
                popUpTo(AppScreens.Home.route) { inclusive = true }
            }
        }
    }

    fun logout(navController: NavController) {
        viewModelScope.launch {
            TokenManager.clearToken()
            _isAuthenticated.value = false
            _successMessage.value = "You have been logged out."
            _protectedResourceData.value = null
            _currentUser.value = null
            _email.value = ""
            _password.value = ""
            _nickname.value = ""
            _isLoginScreen.value = true
            println("Logged out. Token cleared.")
            navController.navigate(AppScreens.Auth.route) {
                popUpTo(AppScreens.Home.route) { inclusive = true }
            }
        }
    }
}
