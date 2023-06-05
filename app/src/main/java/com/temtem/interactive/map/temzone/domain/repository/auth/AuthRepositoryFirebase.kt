package com.temtem.interactive.map.temzone.domain.repository.auth

import android.app.Application
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.GoogleAuthProvider
import com.temtem.interactive.map.temzone.R
import com.temtem.interactive.map.temzone.domain.exception.EmailCollisionException
import com.temtem.interactive.map.temzone.domain.exception.EmailFormatException
import com.temtem.interactive.map.temzone.domain.exception.InvalidCredentialException
import com.temtem.interactive.map.temzone.domain.exception.NetworkException
import com.temtem.interactive.map.temzone.domain.exception.TooManyRequestsException
import com.temtem.interactive.map.temzone.domain.exception.UnknownException
import com.temtem.interactive.map.temzone.domain.exception.WeakPasswordException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryFirebase @Inject constructor(
    private val application: Application,
    private val firebaseAuth: FirebaseAuth,
) : AuthRepository {

    private companion object {
        private const val PASSWORD_MIN_LENGTH = 8
    }

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
                    throw InvalidCredentialException(application)
                }

                is FirebaseNetworkException -> {
                    throw NetworkException(application)
                }

                is FirebaseTooManyRequestsException -> {
                    throw TooManyRequestsException(application)
                }

                else -> {
                    throw UnknownException(application)
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
                    throw InvalidCredentialException(application)
                }

                is FirebaseAuthInvalidCredentialsException -> {
                    throw InvalidCredentialException(application)
                }

                is FirebaseAuthUserCollisionException -> {
                    throw InvalidCredentialException(application)
                }

                is FirebaseNetworkException -> {
                    throw NetworkException(application)
                }

                is FirebaseTooManyRequestsException -> {
                    throw TooManyRequestsException(application)
                }

                else -> {
                    throw UnknownException(application)
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
                    throw InvalidCredentialException(application)
                }

                is FirebaseNetworkException -> {
                    throw NetworkException(application)
                }

                is FirebaseTooManyRequestsException -> {
                    throw TooManyRequestsException(application)
                }

                else -> {
                    throw UnknownException(application)
                }
            }
        }
    }

    override suspend fun signUpWithEmailAndPassword(email: String, password: String) {
        validatePassword(password)

        try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        } catch (exception: Exception) {
            when (exception) {
                is FirebaseAuthWeakPasswordException -> {
                    throw WeakPasswordException(exception.reason.orEmpty())
                }

                is FirebaseAuthInvalidCredentialsException -> {
                    throw EmailFormatException(application)
                }

                is FirebaseAuthUserCollisionException -> {
                    throw EmailCollisionException(application)
                }

                is FirebaseNetworkException -> {
                    throw NetworkException(application)
                }

                is FirebaseTooManyRequestsException -> {
                    throw TooManyRequestsException(application)
                }

                else -> {
                    throw UnknownException(application)
                }
            }
        }
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }

    private fun validatePassword(password: String) {
        if (password.length < PASSWORD_MIN_LENGTH) {
            throw WeakPasswordException(
                application.getString(
                    R.string.password_length_error,
                    PASSWORD_MIN_LENGTH,
                )
            )
        }
        if (!password.contains(Regex("[0-9]"))) {
            throw WeakPasswordException(application.getString(R.string.password_number_error))
        }
        if (password.contains(" ")) {
            throw WeakPasswordException(application.getString(R.string.password_space_error))
        }
    }
}
