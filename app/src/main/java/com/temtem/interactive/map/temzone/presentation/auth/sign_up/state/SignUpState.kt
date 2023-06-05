package com.temtem.interactive.map.temzone.presentation.auth.sign_up.state

sealed interface SignUpState {
    object Empty : SignUpState
    object Loading : SignUpState
    object Success : SignUpState
    data class Error(
        val emailError: String? = null,
        val passwordError: String? = null,
        val confirmPasswordError: String? = null,
        val internalError: String? = null,
        val networkAvailable: Boolean = true,
    ) : SignUpState
}
