package com.temtem.interactive.map.temzone.presentation.auth.forgot_password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.temtem.interactive.map.temzone.core.validation.ValidateNotEmpty
import com.temtem.interactive.map.temzone.domain.exception.EmailFormatException
import com.temtem.interactive.map.temzone.domain.exception.EmailNotFoundException
import com.temtem.interactive.map.temzone.domain.repository.auth.AuthRepository
import com.temtem.interactive.map.temzone.presentation.auth.forgot_password.state.ForgotPasswordFormState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val validateNotEmpty: ValidateNotEmpty,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _forgotPasswordFormState: MutableStateFlow<ForgotPasswordFormState> =
        MutableStateFlow(ForgotPasswordFormState.Empty)
    val forgotPasswordFormState: StateFlow<ForgotPasswordFormState> =
        _forgotPasswordFormState.asStateFlow()

    fun sendPasswordResetEmail(email: String) {
        val emailValidation = validateNotEmpty(email)
        val hasError = listOf(emailValidation).any {
            !it.successful
        }

        if (hasError) {
            _forgotPasswordFormState.update {
                ForgotPasswordFormState.Error(emailMessage = emailValidation.message)
            }
        } else {
            _forgotPasswordFormState.update {
                ForgotPasswordFormState.Loading
            }

            viewModelScope.launch {
                try {
                    authRepository.sendPasswordResetEmail(email)
                    _forgotPasswordFormState.update {
                        ForgotPasswordFormState.Success
                    }
                } catch (exception: Exception) {
                    when (exception) {
                        is EmailFormatException, is EmailNotFoundException -> _forgotPasswordFormState.update {
                            ForgotPasswordFormState.Error(emailMessage = exception.message)
                        }

                        else -> _forgotPasswordFormState.update {
                            ForgotPasswordFormState.Error(snackbarMessage = exception.message)
                        }
                    }
                }
            }
        }
    }
}
