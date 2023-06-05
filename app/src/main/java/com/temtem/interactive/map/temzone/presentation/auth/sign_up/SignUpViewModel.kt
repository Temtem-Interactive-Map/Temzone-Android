package com.temtem.interactive.map.temzone.presentation.auth.sign_up

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.temtem.interactive.map.temzone.R
import com.temtem.interactive.map.temzone.domain.repository.auth.AuthRepository
import com.temtem.interactive.map.temzone.domain.exceptions.EmailCollisionException
import com.temtem.interactive.map.temzone.domain.exceptions.InvalidCredentialException
import com.temtem.interactive.map.temzone.domain.exceptions.NetworkException
import com.temtem.interactive.map.temzone.domain.exceptions.WeakPasswordException
import com.temtem.interactive.map.temzone.presentation.auth.sign_up.state.SignUpState
import com.temtem.interactive.map.temzone.domain.use_case.ValidateConfirmPassword
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
class SignUpViewModel @Inject constructor(
    private val application: Application,
    private val validateEmail: ValidateEmail,
    private val validatePassword: ValidatePassword,
    private val validateConfirmPassword: ValidateConfirmPassword,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _signUpState: MutableStateFlow<SignUpState> = MutableStateFlow(SignUpState.Empty)
    val signUpState: StateFlow<SignUpState> = _signUpState.asStateFlow()

    fun signUp(email: String, password: String, confirmPassword: String) {
        val emailValidation = validateEmail.execute(email)
        val passwordValidation = validatePassword.execute(password)
        val confirmPasswordValidation = validateConfirmPassword.execute(password, confirmPassword)
        val hasError = listOf(emailValidation, passwordValidation, confirmPasswordValidation).any {
            !it.successful
        }

        if (hasError) {
            _signUpState.update {
                SignUpState.Error(
                    emailError = emailValidation.error,
                    passwordError = passwordValidation.error,
                    confirmPasswordError = confirmPasswordValidation.error,
                )
            }
        } else {
            _signUpState.update {
                SignUpState.Loading
            }

            viewModelScope.launch {
                try {
                    authRepository.signUpWithEmailAndPassword(email, password)
                    _signUpState.update {
                        SignUpState.Success
                    }
                } catch (exception: Exception) {
                    when (exception) {
                        is InvalidCredentialException -> {

                        }

                        is EmailCollisionException -> {

                        }

                        is WeakPasswordException -> {

                        }

                        is NetworkException -> {
                            _signUpState.update {
                                SignUpState.Error(
                                    internalError = application.getString(R.string.network_error),
                                    networkAvailable = false,
                                )
                            }
                        }

                        else -> {
                            _signUpState.update {
                                SignUpState.Error(
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
