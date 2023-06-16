package com.temtem.interactive.map.temzone.domain.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.temtem.interactive.map.temzone.R
import com.temtem.interactive.map.temzone.ui.MainActivity
import com.temtem.interactive.map.temzone.ui.MainApplication.Companion.CHANNEL_ID

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        val resultIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("id", message.data["id"])
        }

        val resultPendingIntent = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(resultIntent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setStyle(NotificationCompat.BigTextStyle())
            .setSmallIcon(R.mipmap.ic_launcher).setContentTitle(message.notification?.title)
            .setContentText(message.notification?.body).setAutoCancel(true)
            .setContentIntent(resultPendingIntent)
            .setAutoCancel(true)
            .build()

        with(NotificationManagerCompat.from(this)) {
            val id = System.currentTimeMillis().toString().takeLast(4).toInt()

            notify(id, notification)
        }
    }
}
