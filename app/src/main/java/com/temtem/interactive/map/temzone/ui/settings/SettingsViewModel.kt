package com.temtem.interactive.map.temzone.ui.settings

import androidx.lifecycle.ViewModel
import com.temtem.interactive.map.temzone.repositories.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    fun signOut() {
        authRepository.signOut()
    }
}
