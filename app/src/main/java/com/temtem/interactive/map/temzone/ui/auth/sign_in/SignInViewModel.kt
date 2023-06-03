package com.temtem.interactive.map.temzone.ui.auth.sign_in

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.temtem.interactive.map.temzone.R
import com.temtem.interactive.map.temzone.exceptions.ArgumentException
import com.temtem.interactive.map.temzone.exceptions.NetworkException
import com.temtem.interactive.map.temzone.repositories.auth.AuthRepository
import com.temtem.interactive.map.temzone.ui.auth.sign_in.state.SignInUiState
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
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _signInUiState: MutableStateFlow<SignInUiState> =
        MutableStateFlow(SignInUiState.Empty)
    val signInUiState: StateFlow<SignInUiState> = _signInUiState.asStateFlow()

    fun signIn(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            val emailError =
                if (email.isEmpty()) application.getString(R.string.field_required_error) else null
            val passwordError =
                if (password.isEmpty()) application.getString(R.string.field_required_error) else null

            _signInUiState.update {
                SignInUiState.Error(null, emailError, passwordError)
            }
        } else {
            _signInUiState.update { SignInUiState.Loading }

            viewModelScope.launch {
                try {
                    authRepository.signInWithEmailAndPassword(email, password)
                    _signInUiState.update {
                        SignInUiState.Success
                    }
                } catch (exception: Exception) {
                    when (exception) {
                        is ArgumentException -> {
                            _signInUiState.update {
                                SignInUiState.Error(
                                    null,
                                    application.getString(R.string.sign_in_argument_error),
                                    application.getString(R.string.sign_in_argument_error),
                                )
                            }
                        }

                        is NetworkException -> {
                            _signInUiState.update {
                                SignInUiState.Error(
                                    application.getString(R.string.network_error),
                                    null,
                                    null,
                                )
                            }
                        }

                        else -> {
                            _signInUiState.update {
                                SignInUiState.Error(
                                    application.getString(R.string.unavailable_error),
                                    null,
                                    null,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
