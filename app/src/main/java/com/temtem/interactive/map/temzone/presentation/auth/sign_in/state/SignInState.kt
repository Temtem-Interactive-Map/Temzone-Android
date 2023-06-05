package com.temtem.interactive.map.temzone.presentation.auth.sign_in.state

sealed interface SignInState {
    object Empty : SignInState
    object Loading : SignInState
    object Success : SignInState
    data class Error(
        val emailMessage: String? = null,
        val passwordMessage: String? = null,
        val snackbarMessage: String? = null,
    ) : SignInState
}
