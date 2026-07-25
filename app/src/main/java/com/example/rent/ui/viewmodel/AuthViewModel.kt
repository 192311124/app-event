package com.example.rent.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rent.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val authRepo = AuthRepository()

    private val _currentUser = MutableStateFlow<FirebaseUser?>(authRepo.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    init {
        viewModelScope.launch {
            authRepo.authStateFlow.collect { user ->
                _currentUser.value = user
            }
        }
    }

    fun showToast(msg: String) {
        _toastMessage.value = msg
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun signIn(username: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = authRepo.signIn(username, password)
            _isLoading.value = false

            result.onSuccess {
                showToast("Successfully signed in!")
                onSuccess()
            }.onFailure { e ->
                showToast(e.message ?: "Sign in failed.")
            }
        }
    }

    fun signUp(username: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = authRepo.signUp(username, password)
            _isLoading.value = false

            result.onSuccess {
                showToast("Successfully signed up!")
                onSuccess()
            }.onFailure { e ->
                showToast(e.message ?: "Sign up failed.")
            }
        }
    }

    fun logout() {
        authRepo.logout()
        showToast("Logged out successfully.")
    }
}
