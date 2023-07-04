package com.temtem.interactive.map.temzone.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.temtem.interactive.map.temzone.domain.repository.preference.PreferenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferenceRepository: PreferenceRepository,
) : ViewModel() {

    suspend fun getNotificationPreference(): Boolean {
        return preferenceRepository.getNotificationPreference()
    }

    fun updateNotificationPreference(notificationPreference: Boolean) {
        viewModelScope.launch {
            preferenceRepository.updateNotificationPreference(notificationPreference)
        }
    }
}
