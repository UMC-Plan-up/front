package com.planup.planup.firebase

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
import com.planup.planup.network.repository.NotificationRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PlanupMessagingService() : FirebaseMessagingService() {
    @Inject
    lateinit var notificationRepository: NotificationRepository

    private val channelName = "Planup Channel"
    private val channelId = "planup_channel"

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        if(token.isNotEmpty()) {
            Log.i("PlanupMessagingService", "onNewToken: $token")
            CoroutineScope(Dispatchers.IO).launch {
                notificationRepository.updateFcmToken(token)
            }
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

        // TODO:: 알림 종류에 따라 고도화?
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