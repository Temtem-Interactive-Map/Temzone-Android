package com.temtem.interactive.map.temzone.domain.repository.preference

interface PreferenceRepository {
    suspend fun getNotificationPreference(): Boolean
    suspend fun updateNotificationPreference(notificationPreference: Boolean)
}
