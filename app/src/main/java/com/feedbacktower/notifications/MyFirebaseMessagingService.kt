package com.feedbacktower.notifications


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.graphics.Bitmap
import java.lang.Exception
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.feedbacktower.R
import com.feedbacktower.data.AppPrefs
import com.feedbacktower.ui.SplashScreen
import java.net.HttpURLConnection
import java.net.URL


/**
 * Created by sanket on 02-09-2018.
 */
class MyFirebaseMessagingService : FirebaseMessagingService() {
    companion object {
        private const val TAG = "MyFirebaseMessaging"
    }

    override fun onNewToken(token: String?) {
        super.onNewToken(token)
        AppPrefs.getInstance(this).firebaseToken = token
        Log.d(TAG, "Token: $token")
    }

    override fun onMessageReceived(message: RemoteMessage?) {
        super.onMessageReceived(message)
        if (message == null) {
            Log.d(TAG, "onMessageReceived: message is null")
            return
        }
        Log.d(TAG, "onMessageReceived: Message:  $message")
        Log.d(TAG, "onMessageReceived: From:  ${message.from}")
        if (message.data.isNotEmpty()) {
            Log.d(TAG, "onMessageReceived: Payload " + message.data)
            sendNotification(message)
        }
    }


    private fun sendNotification(remoteMessage: RemoteMessage) {
        val image: String? = remoteMessage.data["image"]
        if (image != null && image.isNotEmpty()) {
            val bitmap = getBitmapFromUrl(image)
            showNotification(remoteMessage, bitmap)
        } else {
            showNotification(remoteMessage)
        }
    }

    private fun getBitmapFromUrl(imageUrl: String): Bitmap? = try {
        val url = URL(imageUrl)
        val connection = url.openConnection() as HttpURLConnection
        connection.doInput = true
        connection.connect()
        val input = connection.inputStream
        BitmapFactory.decodeStream(input)

    } catch (e: Exception) {
        // TODO Auto-generated catch block
        e.printStackTrace()
        null
    }

    private fun showNotification(remoteMessage: RemoteMessage, image: Bitmap? = null) {
        val intent = Intent(this, SplashScreen::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            0
        )
        val builder = NotificationCompat.Builder(this, "notify_001")
        builder.setContentTitle(remoteMessage.data["title"])
        builder.setContentText(remoteMessage.data["body"])
        //builder.setSmallIcon(R.drawable.ic_stat_name)
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        builder.setDefaults(android.app.Notification.DEFAULT_VIBRATE)
        builder.priority = NotificationCompat.PRIORITY_HIGH
        builder.setContentIntent(pendingIntent)
        if (image != null) {
            builder.setLargeIcon(image)
            builder.setStyle(NotificationCompat.BigPictureStyle().bigPicture(image))
        }
        val notification = builder.build()
        val manager = NotificationManagerCompat.from(applicationContext)
        manager.notify(System.currentTimeMillis().toInt(), notification)
    }

}