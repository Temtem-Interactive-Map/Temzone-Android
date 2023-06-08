package com.temtem.interactive.map.temzone.presentation.auth.sign_in.state

sealed interface SignInFormState {
    object Empty : SignInFormState
    object Loading : SignInFormState
    object Success : SignInFormState
    data class Error(
        val emailMessage: String? = null,
        val passwordMessage: String? = null,
        val snackbarMessage: String? = null,
    ) : SignInFormState
}
