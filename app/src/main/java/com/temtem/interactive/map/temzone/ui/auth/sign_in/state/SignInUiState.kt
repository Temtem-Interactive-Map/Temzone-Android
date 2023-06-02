package com.temtem.interactive.map.temzone.ui.auth.sign_in.state

sealed interface SignInUiState {
    object Empty : SignInUiState
    object Loading : SignInUiState
    object Success : SignInUiState
    data class Error(
        val emailError: String?,
        val passwordError: String?,
    ) : SignInUiState
}
