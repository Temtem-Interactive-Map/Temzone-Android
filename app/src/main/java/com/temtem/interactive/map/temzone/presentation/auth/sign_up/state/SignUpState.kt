package com.temtem.interactive.map.temzone.presentation.auth.sign_up.state

sealed interface SignUpState {
    object Empty : SignUpState
    object Loading : SignUpState
    object Success : SignUpState
    data class Error(
        val emailMessage: String? = null,
        val passwordMessage: String? = null,
        val confirmPasswordMessage: String? = null,
        val snackbarMessage: String? = null,
    ) : SignUpState
}
