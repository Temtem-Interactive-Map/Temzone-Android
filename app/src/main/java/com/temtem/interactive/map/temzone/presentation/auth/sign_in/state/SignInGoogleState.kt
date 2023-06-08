package com.temtem.interactive.map.temzone.presentation.auth.sign_in.state

import com.google.android.gms.auth.api.identity.BeginSignInResult

sealed interface SignInGoogleState {
    object Empty : SignInGoogleState
    object Loading : SignInGoogleState
    data class Request(val result: BeginSignInResult) : SignInGoogleState
    object Success : SignInGoogleState
    data class Error(val snackbarMessage: String) : SignInGoogleState
}
