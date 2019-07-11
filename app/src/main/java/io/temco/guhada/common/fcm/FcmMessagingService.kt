package io.temco.guhada.common.fcm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication

/**
 * @author Hyeyeon Park
 */
class FcmMessagingService : FirebaseMessagingService() {
    private val TITLE_IS_EMPTY = "Notification title is empty"
    private val CONTENT_IS_EMPTY = "Notification body is empty"

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)

        // CHECK DATA PAYLOAD
        remoteMessage?.data?.isNotEmpty().let {

        }

        // CHECK NOTIFICATION PAYLOAD
        remoteMessage?.notification?.let {
            showNotification(it.title ?: TITLE_IS_EMPTY, it.body ?: CONTENT_IS_EMPTY)
        }
    }

    private fun showNotification(title: String, content: String) {
        val channelId = applicationContext.getString(R.string.fcm_channel_id)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = applicationContext.getString(R.string.fcm_channel_name)
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(applicationContext, BaseApplication::class.java).apply { flags = Intent.FLAG_ACTIVITY_SINGLE_TOP }
        val requestId = System.currentTimeMillis().toInt()
        val pendingIntent = PendingIntent.getActivities(applicationContext, requestId, arrayOf(intent), PendingIntent.FLAG_UPDATE_CURRENT)

        NotificationCompat.Builder(applicationContext, channelId).let { builder ->
            builder.setContentTitle(title)
                    .setContentText(content)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                    .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                    .setContentIntent(pendingIntent)
            notificationManager.notify(0, builder.build())
        }
    }
}