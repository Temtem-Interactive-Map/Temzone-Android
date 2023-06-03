package com.temtem.interactive.map.temzone.ui.auth.sign_up.state

sealed interface SignUpUiState {

    object Empty : SignUpUiState

    object Loading : SignUpUiState

    object Success : SignUpUiState

    data class Error(
        val emailMessage: String?,
        val passwordMessage: String?,
        val confirmPasswordMessage: String?,
    ) : SignUpUiState
}
