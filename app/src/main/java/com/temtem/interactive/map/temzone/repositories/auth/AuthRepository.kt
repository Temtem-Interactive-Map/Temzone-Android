package com.temtem.interactive.map.temzone.repositories.auth

interface AuthRepository {

    fun isUserSignedIn(): Boolean

    suspend fun getUserToken(): String

    suspend fun signInWithEmailAndPassword(email: String, password: String)

    suspend fun signUpWithEmailAndPassword(email: String, password: String)

    fun signOut()
}
