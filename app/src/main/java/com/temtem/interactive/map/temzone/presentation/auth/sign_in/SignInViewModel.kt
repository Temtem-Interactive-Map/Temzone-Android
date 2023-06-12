package com.temtem.interactive.map.temzone.presentation.auth.sign_in

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.temtem.interactive.map.temzone.core.validation.ValidateNotEmpty
import com.temtem.interactive.map.temzone.domain.exception.InvalidCredentialException
import com.temtem.interactive.map.temzone.domain.repository.auth.AuthRepository
import com.temtem.interactive.map.temzone.presentation.auth.sign_in.state.SignInFormState
import com.temtem.interactive.map.temzone.presentation.auth.sign_in.state.SignInGoogleState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val validateNotEmpty: ValidateNotEmpty,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _signInFormState: MutableStateFlow<SignInFormState> =
        MutableStateFlow(SignInFormState.Empty)
    val signInFormState: StateFlow<SignInFormState> = _signInFormState.asStateFlow()

    fun signInWithEmailAndPassword(email: String, password: String) {
        val emailValidation = validateNotEmpty(email)
        val passwordValidation = validateNotEmpty(password)
        val hasError = listOf(emailValidation, passwordValidation).any {
            !it.successful
        }

        if (hasError) {
            _signInFormState.update {
                SignInFormState.Error(
                    emailMessage = emailValidation.message,
                    passwordMessage = passwordValidation.message,
                )
            }
        } else {
            _signInFormState.update {
                SignInFormState.Loading
            }

            viewModelScope.launch {
                try {
                    authRepository.signInWithEmailAndPassword(email, password)

                    _signInFormState.update {
                        SignInFormState.Success
                    }
                } catch (exception: Exception) {
                    when (exception) {
                        is InvalidCredentialException -> {
                            _signInFormState.update {
                                SignInFormState.Error(
                                    emailMessage = exception.message,
                                    passwordMessage = exception.message,
                                )
                            }
                        }

                        else -> {
                            _signInFormState.update {
                                SignInFormState.Error(snackbarMessage = exception.message)
                            }
                        }
                    }
                }
            }
        }
    }

    private val _signInGoogleState: MutableStateFlow<SignInGoogleState> =
        MutableStateFlow(SignInGoogleState.Empty)
    val signInGoogleState: StateFlow<SignInGoogleState> = _signInGoogleState.asStateFlow()

    fun requestSignInWithGoogle() {
        _signInGoogleState.update {
            SignInGoogleState.Loading
        }

        viewModelScope.launch {
            try {
                val result = authRepository.requestSignInWithGoogle()

                _signInGoogleState.update {
                    SignInGoogleState.Request(result)
                }
            } catch (exception: Exception) {
                _signInGoogleState.update {
                    SignInGoogleState.Error(exception.message.orEmpty())
                }
            }
        }
    }

    fun signInWithGoogle(idToken: String?) {
        _signInGoogleState.update {
            SignInGoogleState.Loading
        }

        viewModelScope.launch {
            try {
                authRepository.signInWithGoogle(idToken)

                _signInGoogleState.update {
                    SignInGoogleState.Success
                }
            } catch (exception: Exception) {
                _signInGoogleState.update {
                    SignInGoogleState.Error(exception.message.orEmpty())
                }
            }
        }
    }
}
