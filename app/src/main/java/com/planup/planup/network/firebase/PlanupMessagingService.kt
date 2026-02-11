package com.planup.planup.network.firebase

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.planup.planup.R
import com.planup.planup.database.TokenSaver
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PlanupMessagingService @Inject constructor(
    private val tokenSaver: TokenSaver
) : FirebaseMessagingService() {
    private val channelName = "Planup Channel"
    private val channelId = "planup_channel"

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        if(token.isNotEmpty()) {
            Log.i("PlanupMessagingService", "Success Fcm NewToken: $token")
            tokenSaver.saveFCMToken(token)
        } else {
            Log.e("PlanupMessagingService", "Failed Fcm NewToken")
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        with(remoteMessage.data) {
            sendNotification(
                title = get("title"),
                body = get("body")
            )
        }
    }

    private fun sendNotification(title: String?, body: String?) {
        if(!checkPermission()) return

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        with(notificationManager) {
            createNotificationChannel(
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            )
        }

        // TODO:: 알림 종류에 따라 고도화
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.img_planup_logo)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(0, notification)
    }

    private fun checkPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }
}