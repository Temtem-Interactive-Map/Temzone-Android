package com.temtem.interactive.map.temzone.domain.repository.auth

import android.app.Application
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.GoogleAuthProvider
import com.temtem.interactive.map.temzone.R
import com.temtem.interactive.map.temzone.domain.exception.AccountException
import com.temtem.interactive.map.temzone.domain.exception.EmailCollisionException
import com.temtem.interactive.map.temzone.domain.exception.EmailFormatException
import com.temtem.interactive.map.temzone.domain.exception.EmailNotFoundException
import com.temtem.interactive.map.temzone.domain.exception.InvalidCredentialException
import com.temtem.interactive.map.temzone.domain.exception.NetworkException
import com.temtem.interactive.map.temzone.domain.exception.TooManyRequestsException
import com.temtem.interactive.map.temzone.domain.exception.UnknownException
import com.temtem.interactive.map.temzone.domain.exception.WeakPasswordException
import com.temtem.interactive.map.temzone.domain.repository.preference.PreferenceRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryFirebase @Inject constructor(
    private val application: Application,
    private val signInClient: SignInClient,
    private val firebaseAuth: FirebaseAuth,
    private val preferenceRepository: PreferenceRepository,
) : AuthRepository {

    private val signInRequest: BeginSignInRequest =
        BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
            .setSupported(true)
            .setFilterByAuthorizedAccounts(true)
            .setServerClientId(application.getString(R.string.default_web_client_id))
            .build().let {
                BeginSignInRequest.builder()
                    .setGoogleIdTokenRequestOptions(it)
                    .setAutoSelectEnabled(true)
                    .build()
            }

    private val signUpRequest: BeginSignInRequest =
        BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
            .setSupported(true)
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(application.getString(R.string.default_web_client_id))
            .build().let {
                BeginSignInRequest.builder()
                    .setGoogleIdTokenRequestOptions(it)
                    .build()
            }

    override fun isUserSignedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    override fun getUserId(): String {
        return firebaseAuth.currentUser?.uid.orEmpty()
    }

    override suspend fun getAuthToken(): String {
        return try {
            firebaseAuth.currentUser?.getIdToken(false)?.await()?.token.orEmpty()
        } catch (exception: Exception) {
            ""
        }
    }

    override suspend fun signInWithEmailAndPassword(email: String, password: String) {
        try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            preferenceRepository.updateNotificationPreference(true)
        } catch (exception: Exception) {
            when (exception) {
                // Thrown if the user account corresponding to email does not exist or has been disabled
                is FirebaseAuthInvalidUserException -> {
                    throw InvalidCredentialException(application)
                }

                // Thrown if the password is wrong
                is FirebaseAuthInvalidCredentialsException -> {
                    throw InvalidCredentialException(application)
                }

                // Thrown if the request has failed due to a network error
                is FirebaseNetworkException -> {
                    throw NetworkException(application)
                }

                // Thrown if the request has been blocked due to excessive consecutive requests
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
        try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            preferenceRepository.updateNotificationPreference(true)
        } catch (exception: Exception) {
            when (exception) {
                // Thrown if the password is not strong enough
                is FirebaseAuthWeakPasswordException -> {
                    throw WeakPasswordException(application)
                }

                // Thrown if the email address is malformed
                is FirebaseAuthInvalidCredentialsException -> {
                    throw EmailFormatException(application)
                }

                // Thrown if there already exists an account with the given email address
                is FirebaseAuthUserCollisionException -> {
                    throw EmailCollisionException(application)
                }

                // Thrown if the request has failed due to a network error
                is FirebaseNetworkException -> {
                    throw NetworkException(application)
                }

                // Thrown if the request has been blocked due to excessive consecutive requests
                is FirebaseTooManyRequestsException -> {
                    throw TooManyRequestsException(application)
                }

                else -> {
                    throw UnknownException(application)
                }
            }
        }
    }

    override suspend fun requestSignInWithGoogle(): BeginSignInResult {
        return try {
            signInClient.beginSignIn(signInRequest).await()
        } catch (exception: ApiException) {
            try {
                signInClient.beginSignIn(signUpRequest).await()
            } catch (exception: ApiException) {
                throw UnknownException(application)
            }
        }
    }

    override suspend fun signInWithGoogle(idToken: String?) {
        try {
            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)

            firebaseAuth.signInWithCredential(firebaseCredential).await()
            preferenceRepository.updateNotificationPreference(true)
        } catch (exception: Exception) {
            when (exception) {
                // Thrown if the user has been disabled
                is FirebaseAuthInvalidUserException -> {
                    throw AccountException(application)
                }

                // Thrown if the credential is malformed or has expired
                is FirebaseAuthInvalidCredentialsException -> {
                    throw UnknownException(application)
                }

                // Thrown if there already exists an account with the email address
                is FirebaseAuthUserCollisionException -> {
                    throw EmailCollisionException(application)
                }

                // Thrown if the request has failed due to a network error
                is FirebaseNetworkException -> {
                    throw NetworkException(application)
                }

                // Thrown if the request has been blocked due to excessive consecutive requests
                is FirebaseTooManyRequestsException -> {
                    throw TooManyRequestsException(application)
                }

                else -> {
                    throw UnknownException(application)
                }
            }
        }
    }

    override suspend fun sendPasswordResetEmail(email: String) {
        try {
            firebaseAuth.sendPasswordResetEmail(email).await()
        } catch (exception: Exception) {
            when (exception) {
                // Thrown if the email address is malformed
                is FirebaseAuthInvalidCredentialsException -> {
                    throw EmailFormatException(application)
                }

                // Thrown if there is no user corresponding to the given email address
                is FirebaseAuthInvalidUserException -> {
                    throw EmailNotFoundException(application)
                }

                // Thrown if the request has failed due to a network error
                is FirebaseNetworkException -> {
                    throw NetworkException(application)
                }

                // Thrown if the request has been blocked due to excessive consecutive requests
                is FirebaseTooManyRequestsException -> {
                    throw TooManyRequestsException(application)
                }

                else -> {
                    throw UnknownException(application)
                }
            }
        }
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
        signInClient.signOut().await()
        preferenceRepository.updateNotificationPreference(false)
    }
}
