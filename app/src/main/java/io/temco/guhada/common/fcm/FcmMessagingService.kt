package io.temco.guhada.common.fcm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.text.TextUtils
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.data.model.GuhadaNotiMessage
import org.json.JSONObject

/**
 * @author Hyeyeon Park
 */
class FcmMessagingService : FirebaseMessagingService() {
    private val TITLE_IS_EMPTY = "구하다 알림"
    private val CONTENT_IS_EMPTY = "Notification body is empty"

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)
        try {
            // CHECK DATA PAYLOAD
            remoteMessage?.data?.isNotEmpty().let {
                if(CustomLog.flag)CustomLog.L("showNotification","it",it.toString())
                if(it?:false){
                    if(CustomLog.flag)CustomLog.L("showNotification","it",remoteMessage!!.data!!.toString())
                    var message = GuhadaNotiMessage()
                    if(remoteMessage?.data!!.containsKey("scheme")) message.scheme = remoteMessage?.data?.get("scheme") ?: ""
                    if(remoteMessage?.data!!.containsKey("image")) message.image = remoteMessage?.data?.get("image") ?: ""
                    if(remoteMessage?.data!!.containsKey("message")) message.message = remoteMessage?.data?.get("message") ?: ""
                    if(remoteMessage?.data!!.containsKey("title")) message.title = remoteMessage?.data?.get("title") ?: ""
                    /*var json = remoteMessage!!.data!!.toString()
                    var message = Gson().fromJson<GuhadaNotiMessage>(json, GuhadaNotiMessage::class.java)
                    if(CustomLog.flag)CustomLog.L("showNotification","message",message.toString())*/
                    if(CustomLog.flag)CustomLog.L("showNotification","message",message.toString())
                    showNotification(if(TextUtils.isEmpty(message.title)) TITLE_IS_EMPTY else message.title ?: "", message.message ?: CONTENT_IS_EMPTY)
                }
            }

            // CHECK NOTIFICATION PAYLOAD
            /*remoteMessage?.notification?.let {
                if(CustomLog.flag)CustomLog.L("showNotification","it",it.body.toString())
                showNotification(it.title ?: TITLE_IS_EMPTY, it.body ?: CONTENT_IS_EMPTY)
            }*/
        }catch (e : Exception){
            if(CustomLog.flag)CustomLog.E(e)
        }
    }

    private fun showNotification(title: String, content: String) {
        if(CustomLog.flag)CustomLog.L("showNotification","title",title,"content",content)
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