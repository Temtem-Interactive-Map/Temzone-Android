package com.temtem.interactive.map.temzone.domain.repository.auth

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.GoogleAuthProvider
import com.temtem.interactive.map.temzone.domain.exceptions.EmailCollisionException
import com.temtem.interactive.map.temzone.domain.exceptions.InternalException
import com.temtem.interactive.map.temzone.domain.exceptions.InvalidCredentialException
import com.temtem.interactive.map.temzone.domain.exceptions.NetworkException
import com.temtem.interactive.map.temzone.domain.exceptions.TooManyRequestsException
import com.temtem.interactive.map.temzone.domain.exceptions.WeakPasswordException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryFirebase @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
) : AuthRepository {

    override fun isUserSignedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    override suspend fun getAuthToken(): String {
        return try {
            firebaseAuth.currentUser?.getIdToken(true)?.await()?.token.orEmpty()
        } catch (exception: Exception) {
            ""
        }
    }

    override suspend fun signInWithEmailAndPassword(email: String, password: String) {
        try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
        } catch (exception: Exception) {
            when (exception) {
                is FirebaseAuthInvalidUserException, is FirebaseAuthInvalidCredentialsException -> {
                    throw InvalidCredentialException(exception)
                }

                is FirebaseNetworkException -> {
                    throw NetworkException(exception)
                }

                is FirebaseTooManyRequestsException -> {
                    throw TooManyRequestsException(exception)
                }

                else -> {
                    throw InternalException(exception)
                }
            }
        }
    }

    override suspend fun signInWithGoogle(idToken: String) {
        try {
            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)

            firebaseAuth.signInWithCredential(firebaseCredential).await()
        } catch (exception: Exception) {
            when (exception) {
                is FirebaseAuthInvalidUserException -> {
                    throw InvalidCredentialException(exception)
                }

                is FirebaseAuthInvalidCredentialsException -> {
                    throw InvalidCredentialException(exception)
                }

                is FirebaseAuthUserCollisionException -> {
                    throw InvalidCredentialException(exception)
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

    override suspend fun forgotPassword(email: String) {
        try {
            firebaseAuth.sendPasswordResetEmail(email).await()
        } catch (exception: Exception) {
            when (exception) {
                is FirebaseAuthInvalidUserException -> {
                    throw InvalidCredentialException(exception)
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
                    throw WeakPasswordException(exception)
                }

                is FirebaseAuthInvalidCredentialsException -> {
                    throw InvalidCredentialException(exception)
                }

                is FirebaseAuthUserCollisionException -> {
                    throw EmailCollisionException(exception)
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
