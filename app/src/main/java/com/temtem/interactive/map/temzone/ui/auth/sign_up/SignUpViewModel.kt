package com.temtem.interactive.map.temzone.ui.auth.sign_up

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.temtem.interactive.map.temzone.R
import com.temtem.interactive.map.temzone.repositories.auth.AuthRepository
import com.temtem.interactive.map.temzone.ui.auth.sign_up.state.SignUpUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val application: Application,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _signUpUiState: MutableStateFlow<SignUpUiState> =
        MutableStateFlow(SignUpUiState.Empty)
    val signUpUiState: StateFlow<SignUpUiState> = _signUpUiState.asStateFlow()

    fun signUp(email: String, password: String, confirmPassword: String) {
        if (email.isEmpty() || password.isEmpty()) {
            val emailError =
                if (email.isEmpty()) application.getString(R.string.field_required_error) else null
            val passwordError =
                if (password.isEmpty()) application.getString(R.string.field_required_error) else null
            val confirmPasswordError =
                if (confirmPassword.isEmpty()) application.getString(R.string.field_required_error) else null

            _signUpUiState.update {
                SignUpUiState.Error(emailError, passwordError, confirmPasswordError)
            }
        } else {
            _signUpUiState.update { SignUpUiState.Loading }

            viewModelScope.launch {
                try {
                    authRepository.signUpWithEmailAndPassword(email, password)
                    _signUpUiState.update { SignUpUiState.Success }
                } catch (e: Exception) {
                    _signUpUiState.update {
                        SignUpUiState.Error(
                            e.message.orEmpty(),
                            e.message.orEmpty(),
                            e.message.orEmpty(),
                        )
                    }
                }
            }
        }
    }
}
