package com.caretail.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caretail.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(
    private val repository: FirebaseRepository = FirebaseRepository()
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    init {
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        viewModelScope.launch {
            if (repository.isUserLoggedIn()) {
                val result = repository.getCurrentUser()
                result.onSuccess { user ->
                    _currentUser.value = user
                    _authState.value = AuthState.Success(user)
                }.onFailure {
                    _authState.value = AuthState.Idle
                }
            }
        }
    }

    /**
     * Register new user - handles both owners and sitters
     */
    fun register(
        email: String,
        password: String,
        name: String,
        phone: String,
        dob: String,
        role: Role,
        address: String = "",
        workHours: String = "",
        expertise: String = "",
        location: String = ""
    ) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            val result = repository.registerUser(
                email = email,
                password = password,
                name = name,
                phone = phone,
                dob = dob,
                role = role,
                address = address,
                workHours = workHours,
                expertise = expertise,
                location = location
            )

            result.onSuccess { user ->
                _currentUser.value = user
                _authState.value = AuthState.Success(user)

            }.onFailure { exception ->
                _authState.value = AuthState.Error(
                    exception.message ?: "Registration failed. Please try again."
                )
            }
        }
    }

    /**
     * Login user
     */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            val result = repository.loginUser(email, password)

            result.onSuccess { user ->
                _currentUser.value = user
                _authState.value = AuthState.Success(user)
            }.onFailure { exception ->
                _authState.value = AuthState.Error(
                    exception.message ?: "Login failed. Please check your credentials."
                )
            }
        }
    }

    /**
     * Logout user
     */
    fun logout() {
        repository.logout()
        _currentUser.value = null
        _authState.value = AuthState.Idle
    }

    /**
     * Send password reset email
     */
    fun sendPasswordReset(email: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.sendPasswordResetEmail(email)
            result.onSuccess {
                onResult(true, "Password reset email sent. Please check your inbox.")
            }.onFailure { exception ->
                onResult(false, exception.message ?: "Failed to send reset email.")
            }
        }
    }

    /**
     * Reset auth state to idle
     */
    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }

    /**
     * Update user profile
     */
    fun updateProfile(updates: Map<String, Any>, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val userId = _currentUser.value?.id ?: return@launch

            val result = repository.updateUser(userId, updates)
            result.onSuccess {
                val userResult = repository.getUserById(userId)
                userResult.onSuccess { user ->
                    _currentUser.value = user
                    onResult(true, "Profile updated successfully")
                }
            }.onFailure { exception ->
                onResult(false, exception.message ?: "Failed to update profile")
            }
        }
    }

    fun getUserRole(): Role? = _currentUser.value?.getRoleEnum()
    fun isOwner(): Boolean = getUserRole() == Role.OWNER
    fun isSitter(): Boolean = getUserRole() == Role.SITTER
}
