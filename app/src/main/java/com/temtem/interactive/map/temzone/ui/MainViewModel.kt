package com.temtem.interactive.map.temzone.ui

import androidx.lifecycle.ViewModel
import com.temtem.interactive.map.temzone.repositories.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    fun isUserSignedIn(): Boolean {
        return authRepository.isUserSignedIn()
    }
}
