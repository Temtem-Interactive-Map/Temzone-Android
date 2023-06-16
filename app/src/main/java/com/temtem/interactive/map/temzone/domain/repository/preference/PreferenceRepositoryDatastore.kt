package com.temtem.interactive.map.temzone.domain.repository.preference

import android.app.Application
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.google.firebase.messaging.FirebaseMessaging
import com.temtem.interactive.map.temzone.core.extension.datastore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PreferenceRepositoryDatastore @Inject constructor(
    private val application: Application,
    private val firebaseMessaging: FirebaseMessaging,
) : PreferenceRepository {

    private companion object {
        private const val TOPIC_EN = "en"
        private val NOTIFICATION = booleanPreferencesKey("notification")
    }

    override suspend fun getNotificationPreference(): Boolean {
        return application.datastore.data.map {
            it[NOTIFICATION] ?: true
        }.first()
    }

    override suspend fun updateNotificationPreference(notificationPreference: Boolean) {
        if (notificationPreference) {
            firebaseMessaging.subscribeToTopic(TOPIC_EN)
        } else {
            firebaseMessaging.unsubscribeFromTopic(TOPIC_EN)
        }

        application.datastore.edit {
            it[NOTIFICATION] = notificationPreference
        }
    }
}
