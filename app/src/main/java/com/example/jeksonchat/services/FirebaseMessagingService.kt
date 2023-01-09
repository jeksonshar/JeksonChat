package com.example.jeksonchat.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.toBitmap
import com.example.jeksonchat.R
import com.example.jeksonchat.presentation.ui.chat.ChatActivity
import com.example.jeksonchat.utils.SENDER_ID
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class FirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        message.data.let {
            val titleMessage = it["title"] ?: "No title"
            val textMessage = it["body"] ?: "No text"
            val recipientId = it["recipientId"] ?: ""
            val senderId = it["senderId"] ?: ""

            Log.d("TAG", "onMessageReceived: title = $titleMessage, senderId = $senderId, recipientId = $recipientId")
            if (Firebase.auth.currentUser?.uid == recipientId) {
                sendNotification(titleMessage, textMessage, senderId)
            }
        }
    }

    override fun onNewToken(token: String) {
        Log.d("TAG", "onNewToken: = $token")
    }


    private fun sendNotification(title: String, text: String, senderId: String) {
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val intent = Intent(this, ChatActivity::class.java)
        Log.d("TAG", "sendNotification: senderId = $senderId")
        intent.putExtra(SENDER_ID, senderId)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val channelId = getString(R.string.default_notification_channel_id)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.send_icon)
            .setLargeIcon(AppCompatResources.getDrawable(this, R.drawable.chat_message_icon)?.toBitmap())
            .setContentTitle(title)
            .setContentText(text)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Channel name", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(Random(System.currentTimeMillis()).nextInt(1000), notificationBuilder.build())

    }

}