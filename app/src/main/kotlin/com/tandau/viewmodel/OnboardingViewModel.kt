package com.tandau.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tandau.data.local.entities.*
import com.tandau.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Patterns

class OnboardingViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _interests = MutableStateFlow<List<Interest>>(emptyList())
    val interests: StateFlow<List<Interest>> = _interests

    private val _strengths = MutableStateFlow<List<Strength>>(emptyList())
    val strengths: StateFlow<List<Strength>> = _strengths

    private val _selectedInterests = MutableStateFlow<Set<Long>>(emptySet())
    val selectedInterests: StateFlow<Set<Long>> = _selectedInterests

    private val _selectedStrengths = MutableStateFlow<Set<Long>>(emptySet())
    val selectedStrengths: StateFlow<Set<Long>> = _selectedStrengths

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _savedSuccess = MutableStateFlow(false)
    val savedSuccess: StateFlow<Boolean> = _savedSuccess

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val ints = userRepository.getAllInterests()
            val strs = userRepository.getAllStrengths()

            // Seed data if empty
            if (ints.isEmpty()) {
                val seedInterests = listOf("Technology", "Music", "Art", "Sports", "Gaming", "Cooking", "Travel", "Business").map { Interest(name = it) }
                userRepository.saveInterests(seedInterests)
                _interests.value = userRepository.getAllInterests()
            } else {
                _interests.value = ints
            }

            if (strs.isEmpty()) {
                val seedStrengths = listOf("Leadership", "Communication", "Creativity", "Coding", "Analysis", "Design", "Problem Solving", "Teamwork").map { Strength(name = it) }
                userRepository.saveStrengths(seedStrengths)
                _strengths.value = userRepository.getAllStrengths()
            } else {
                _strengths.value = strs
            }
        }
    }

    fun updateSelectedInterests(selected: Set<Long>) {
        _selectedInterests.value = selected
    }

    fun updateSelectedStrengths(selected: Set<Long>) {
        _selectedStrengths.value = selected
    }

    fun validateDetails(email: String, password: String, username: String, city: String): String? {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return "Invalid email format"
        }
        val domain = email.substringAfterLast("@", "")
        val allowedDomains = listOf("gmail.com", "outlook.com", "yahoo.com")
        if (domain !in allowedDomains) {
             return "Email must be from gmail.com, outlook.com, or yahoo.com"
        }
        if (password.length < 6) {
            return "Password must be at least 6 characters"
        }
        if (username.isBlank()) {
            return "Username is required"
        }
        if (city.isBlank()) {
            return "City is required"
        }
        return null
    }

    fun saveUserProfile(email: String, password: String, username: String, city: String, age: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // Check if user exists
                val existingUser = userRepository.getUserByEmail(email)
                if (existingUser != null) {
                    _error.value = "User already exists"
                    return@launch
                }

                // 1. Save Profile
                val userId = userRepository.saveUserProfile(
                    UserProfile(
                        email = email,
                        password = password,
                        username = username,
                        city = city,
                        age = age
                    )
                )

                // 2. Save Interests
                val userInterests = _selectedInterests.value.map { UserInterest(userId = userId, interestId = it) }
                userRepository.saveUserInterests(userInterests)

                // 3. Save Strengths
                val userStrengths = _selectedStrengths.value.map { UserStrength(userId = userId, strengthId = it) }
                userRepository.saveUserStrengths(userStrengths)

                // 4. Create Session
                userRepository.createSession(userId, email)

                _savedSuccess.value = true
            } catch (e: Exception) {
                _error.value = "An error occurred. Please try again."
            } finally {
                _isLoading.value = false
            }
        }
    }
}
