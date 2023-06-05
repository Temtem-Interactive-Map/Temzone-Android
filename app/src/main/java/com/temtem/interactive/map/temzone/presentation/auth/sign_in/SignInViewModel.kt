package com.temtem.interactive.map.temzone.presentation.auth.sign_in

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.temtem.interactive.map.temzone.core.validation.ValidateNotEmpty
import com.temtem.interactive.map.temzone.domain.exception.InvalidCredentialException
import com.temtem.interactive.map.temzone.domain.repository.auth.AuthRepository
import com.temtem.interactive.map.temzone.presentation.auth.sign_in.state.SignInState
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

    private val _signInState: MutableStateFlow<SignInState> = MutableStateFlow(SignInState.Empty)
    val signInState: StateFlow<SignInState> = _signInState.asStateFlow()

    fun signIn(email: String, password: String) {
        val emailValidation = validateNotEmpty(email)
        val passwordValidation = validateNotEmpty(password)
        val hasError = listOf(emailValidation, passwordValidation).any {
            !it.successful
        }

        if (hasError) {
            _signInState.update {
                SignInState.Error(
                    emailMessage = emailValidation.message,
                    passwordMessage = passwordValidation.message,
                )
            }
        } else {
            _signInState.update {
                SignInState.Loading
            }
            viewModelScope.launch {
                try {
                    authRepository.signInWithEmailAndPassword(email, password)
                    _signInState.update {
                        SignInState.Success
                    }
                } catch (exception: Exception) {
                    when (exception) {
                        is InvalidCredentialException -> {
                            _signInState.update {
                                SignInState.Error(
                                    emailMessage = exception.message,
                                    passwordMessage = exception.message,
                                )
                            }
                        }

                        else -> {
                            _signInState.update {
                                SignInState.Error(snackbarMessage = exception.message)
                            }
                        }
                    }
                }
            }
        }
    }
}
