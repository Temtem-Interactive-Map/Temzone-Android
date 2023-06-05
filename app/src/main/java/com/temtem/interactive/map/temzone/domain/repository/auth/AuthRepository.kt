package com.temtem.interactive.map.temzone.domain.repository.auth

interface AuthRepository {
    fun isUserSignedIn(): Boolean
    suspend fun getAuthToken(): String
    suspend fun signInWithEmailAndPassword(email: String, password: String)
    suspend fun signInWithGoogle(idToken: String)
    suspend fun forgotPassword(email: String)
    suspend fun signUpWithEmailAndPassword(email: String, password: String)
    fun signOut()
}
