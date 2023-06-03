package com.temtem.interactive.map.temzone.repositories.auth

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.temtem.interactive.map.temzone.exceptions.ArgumentException
import com.temtem.interactive.map.temzone.exceptions.InternalException
import com.temtem.interactive.map.temzone.exceptions.NetworkException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
) : AuthRepository {

    override fun isUserSignedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    override suspend fun getUserToken(): String {
        try {
            return firebaseAuth.currentUser?.getIdToken(false)?.await()?.token.orEmpty()
        } catch (exception: Exception) {
            when (exception) {
                is FirebaseNetworkException -> {
                    throw NetworkException(exception)
                }

                else -> {
                    throw InternalException(exception)
                }
            }
        }
    }

    override suspend fun signInWithEmailAndPassword(email: String, password: String) {
        try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
        } catch (exception: Exception) {
            when (exception) {
                is FirebaseAuthInvalidUserException, is FirebaseAuthInvalidCredentialsException -> {
                    throw ArgumentException(exception)
                }

                is FirebaseNetworkException -> {
                    throw NetworkException(exception)
                }

                else -> {
                    throw InternalException(exception)
                }
            }
        }
    }

    override suspend fun signUpWithEmailAndPassword(email: String, password: String) {
        try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        } catch (exception: Exception) {
            when (exception) {
                is FirebaseAuthWeakPasswordException -> {
                    throw ArgumentException(exception)
                }

                is FirebaseAuthInvalidCredentialsException -> {
                    throw ArgumentException(exception)
                }

                is FirebaseAuthUserCollisionException -> {
                    throw ArgumentException(exception)
                }

                is FirebaseNetworkException -> {
                    throw NetworkException(exception)
                }

                else -> {
                    throw InternalException(exception)
                }
            }
        }
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }
}
