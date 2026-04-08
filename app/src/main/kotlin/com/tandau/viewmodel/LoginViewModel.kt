package com.tandau.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tandau.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _error.value = "Please enter email and password"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val user = userRepository.getUserByEmail(email)
                if (user != null) {
                    if (user.password == password) {
                        userRepository.createSession(user.id, user.email)
                        _loginSuccess.value = true
                    } else {
                        _error.value = "Incorrect password"
                    }
                } else {
                    _error.value = "User not found. Please sign up."
                }
            } catch (e: Exception) {
                _error.value = "An error occurred. Please try again."
            } finally {
                _isLoading.value = false
            }
        }
    }
}
