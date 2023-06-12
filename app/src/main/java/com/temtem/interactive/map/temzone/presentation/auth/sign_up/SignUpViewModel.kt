package com.temtem.interactive.map.temzone.presentation.auth.sign_up

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.temtem.interactive.map.temzone.core.validation.ValidateMatchPassword
import com.temtem.interactive.map.temzone.core.validation.ValidateNotEmpty
import com.temtem.interactive.map.temzone.domain.exception.EmailCollisionException
import com.temtem.interactive.map.temzone.domain.exception.EmailFormatException
import com.temtem.interactive.map.temzone.domain.exception.WeakPasswordException
import com.temtem.interactive.map.temzone.domain.repository.auth.AuthRepository
import com.temtem.interactive.map.temzone.presentation.auth.sign_up.state.SignUpFormState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val validateNotEmpty: ValidateNotEmpty,
    private val validateMatchPassword: ValidateMatchPassword,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _signUpFormState: MutableStateFlow<SignUpFormState> =
        MutableStateFlow(SignUpFormState.Empty)
    val signUpFormState: StateFlow<SignUpFormState> = _signUpFormState.asStateFlow()

    fun signUp(email: String, password: String, confirmPassword: String) {
        val emailValidation = validateNotEmpty(email)
        val passwordValidation = validateNotEmpty(password)
        val confirmPasswordValidation = validateMatchPassword(password, confirmPassword)
        val hasError = listOf(emailValidation, passwordValidation, confirmPasswordValidation).any {
            !it.successful
        }

        if (hasError) {
            _signUpFormState.update {
                SignUpFormState.Error(
                    emailMessage = emailValidation.message,
                    passwordMessage = passwordValidation.message,
                    confirmPasswordMessage = confirmPasswordValidation.message,
                )
            }
        } else {
            _signUpFormState.update {
                SignUpFormState.Loading
            }

            viewModelScope.launch {
                try {
                    authRepository.signUpWithEmailAndPassword(email, password)

                    _signUpFormState.update {
                        SignUpFormState.Success
                    }
                } catch (exception: Exception) {
                    when (exception) {
                        is EmailFormatException, is EmailCollisionException -> {
                            _signUpFormState.update {
                                SignUpFormState.Error(
                                    emailMessage = exception.message,
                                )
                            }
                        }

                        is WeakPasswordException -> {
                            _signUpFormState.update {
                                SignUpFormState.Error(
                                    passwordMessage = exception.message,
                                )
                            }
                        }

                        else -> {
                            _signUpFormState.update {
                                SignUpFormState.Error(
                                    snackbarMessage = exception.message,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
