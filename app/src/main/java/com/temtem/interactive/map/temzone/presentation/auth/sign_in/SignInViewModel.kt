package com.temtem.interactive.map.temzone.presentation.auth.sign_in

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.temtem.interactive.map.temzone.R
import com.temtem.interactive.map.temzone.domain.repository.auth.AuthRepository
import com.temtem.interactive.map.temzone.domain.exceptions.InvalidCredentialException
import com.temtem.interactive.map.temzone.domain.exceptions.NetworkException
import com.temtem.interactive.map.temzone.presentation.auth.sign_in.state.SignInState
import com.temtem.interactive.map.temzone.domain.use_case.ValidateEmail
import com.temtem.interactive.map.temzone.domain.use_case.ValidatePassword
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val application: Application,
    private val validateEmail: ValidateEmail,
    private val validatePassword: ValidatePassword,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _signInState: MutableStateFlow<SignInState> = MutableStateFlow(SignInState.Empty)
    val signInState: StateFlow<SignInState> = _signInState.asStateFlow()

    fun signIn(email: String, password: String) {
        val emailValidation = validateEmail.execute(email)
        val passwordValidation = validatePassword.execute(password)
        val hasError = listOf(emailValidation, passwordValidation).any {
            !it.successful
        }

        if (hasError) {
            _signInState.update {
                SignInState.Error(
                    emailError = emailValidation.error,
                    passwordError = passwordValidation.error,
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
                                    emailError = application.getString(R.string.credential_error),
                                    passwordError = application.getString(R.string.credential_error),
                                )
                            }
                        }

                        is NetworkException -> {
                            _signInState.update {
                                SignInState.Error(
                                    internalError = application.getString(R.string.network_error),
                                    networkAvailable = false,
                                )
                            }
                        }

                        else -> {
                            _signInState.update {
                                SignInState.Error(
                                    internalError = application.getString(R.string.internal_error),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
