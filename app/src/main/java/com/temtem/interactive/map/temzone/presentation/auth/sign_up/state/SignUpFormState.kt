package com.temtem.interactive.map.temzone.presentation.auth.sign_up.state

sealed interface SignUpFormState {
    object Empty : SignUpFormState
    object Loading : SignUpFormState
    object Success : SignUpFormState
    data class Error(
        val emailMessage: String? = null,
        val passwordMessage: String? = null,
        val confirmPasswordMessage: String? = null,
        val snackbarMessage: String? = null,
    ) : SignUpFormState
}
