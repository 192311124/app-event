package com.example.rent.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    companion object {
        fun usernameToEmail(username: String): String {
            val clean = username.trim().lowercase().replace(Regex("[^a-z0-9_.-]"), "")
            return "$clean@vibecraft.com"
        }

        fun validatePassword(password: String): Pair<Boolean, String> {
            if (password.length < 6) {
                return Pair(false, "Password must be at least 6 characters long.")
            }
            if (!password.contains(Regex("[A-Z]"))) {
                return Pair(false, "Password must contain at least 1 uppercase letter.")
            }
            if (!password.contains(Regex("[0-9]"))) {
                return Pair(false, "Password must contain at least 1 number.")
            }
            if (!password.contains(Regex("[!@#$%^&*(),.?\":{}|<>]"))) {
                return Pair(false, "Password must contain at least 1 special character.")
            }
            return Pair(true, "Password is valid.")
        }
    }

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    val currentUsername: String?
        get() = auth.currentUser?.email?.split("@")?.get(0)

    val authStateFlow: Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    suspend fun signIn(username: String, password: String): Result<FirebaseUser?> {
        if (username.isBlank() || password.isBlank()) {
            return Result.failure(Exception("Please fill in both fields."))
        }
        return try {
            val email = usernameToEmail(username)
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(authResult.user)
        } catch (e: Exception) {
            Result.failure(Exception(formatAuthError(e)))
        }
    }

    suspend fun signUp(username: String, password: String): Result<FirebaseUser?> {
        val (isValid, message) = validatePassword(password)
        if (!isValid) {
            return Result.failure(Exception(message))
        }
        if (username.trim().length < 3) {
            return Result.failure(Exception("Username must be at least 3 characters long."))
        }

        return try {
            val email = usernameToEmail(username)
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            Result.success(authResult.user)
        } catch (e: Exception) {
            Result.failure(Exception(formatAuthError(e)))
        }
    }

    fun logout() {
        auth.signOut()
    }

    private fun formatAuthError(e: Exception): String {
        val msg = e.message ?: ""
        if (e is com.google.firebase.auth.FirebaseAuthUserCollisionException ||
            msg.contains("email-already-in-use", ignoreCase = true) ||
            msg.contains("already in use", ignoreCase = true) ||
            msg.contains("already exists", ignoreCase = true) ||
            msg.contains("duplicate", ignoreCase = true)
        ) {
            return "Username already exists. Please sign in instead."
        }
        if (msg.contains("user-not-found", ignoreCase = true) ||
            msg.contains("wrong-password", ignoreCase = true) ||
            msg.contains("invalid-credential", ignoreCase = true)
        ) {
            return "Invalid username or password."
        }
        if (msg.contains("api-key-not-valid", ignoreCase = true)) {
            return "Invalid Firebase API Key in project configuration."
        }

        val sanitized = msg
            .replace("email address", "username", ignoreCase = true)
            .replace("email", "username", ignoreCase = true)

        return if (sanitized.isNotBlank()) sanitized else "Authentication failed."
    }
}
