package com.temtem.interactive.map.temzone.ui

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.temtem.interactive.map.temzone.R
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication : Application() {

    companion object {
        const val CHANNEL_ID = "TEMZONE_CHANNEL"
    }

    override fun onCreate() {
        super.onCreate()

        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.app_name),
            NotificationManager.IMPORTANCE_DEFAULT,
        )
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(channel)
    }
}
