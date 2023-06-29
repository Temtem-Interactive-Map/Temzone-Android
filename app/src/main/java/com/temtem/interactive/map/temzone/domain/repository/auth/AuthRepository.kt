package com.temtem.interactive.map.temzone.domain.repository.auth

import com.google.android.gms.auth.api.identity.BeginSignInResult

interface AuthRepository {
    fun isUserSignedIn(): Boolean
    fun getUserId(): String
    suspend fun getAuthToken(): String
    suspend fun signInWithEmailAndPassword(email: String, password: String)
    suspend fun signUpWithEmailAndPassword(email: String, password: String)
    suspend fun requestSignInWithGoogle(): BeginSignInResult
    suspend fun signInWithGoogle(idToken: String?)
    suspend fun sendPasswordResetEmail(email: String)
    suspend fun signOut()
}
