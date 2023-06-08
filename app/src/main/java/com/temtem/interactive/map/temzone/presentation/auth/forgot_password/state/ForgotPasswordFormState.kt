package com.temtem.interactive.map.temzone.presentation.auth.forgot_password.state

sealed interface ForgotPasswordFormState {
    object Empty : ForgotPasswordFormState
    object Loading : ForgotPasswordFormState
    object Success : ForgotPasswordFormState
    data class Error(
        val emailMessage: String? = null,
        val snackbarMessage: String? = null,
    ) : ForgotPasswordFormState
}
