package io.musicorum.mobile.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.musicorum.mobile.R

class MessagingService : FirebaseMessagingService() {
    companion object {
        fun createNotificationChannel(ctx: Context) {
            val name = "General"
            val descriptionText = "General notifications related to Musicorum"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("General", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(ctx, NotificationManager::class.java) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onNewToken(token: String) {
        Log.d("FCM", "New token generated: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        Log.d("FCM", "onMessageReceived")

        val builder = NotificationCompat.Builder(this.applicationContext, "General")
            .setSmallIcon(R.drawable.ic_stat_ic_notification)
            .setAutoCancel(true)
            .setContentTitle(message.notification!!.title)
            .setContentText(message.notification!!.body)
            .build()

        with(NotificationManagerCompat.from(this.applicationContext)) {
            notify(message.messageId?.toIntOrNull() ?: 0, builder)
        }
    }
}