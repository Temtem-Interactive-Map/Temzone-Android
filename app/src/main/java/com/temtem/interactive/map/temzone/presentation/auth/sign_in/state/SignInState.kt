package com.temtem.interactive.map.temzone.presentation.auth.sign_in.state

sealed interface SignInState {
    object Empty : SignInState
    object Loading : SignInState
    object Success : SignInState
    data class Error(
        val emailError: String? = null,
        val passwordError: String? = null,
        val internalError: String? = null,
        val networkAvailable: Boolean = true,
    ) : SignInState
}
