package com.production.monoprix

import android.annotation.SuppressLint
import android.app.*
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.production.monoprix.ui.splash.SplashScreen
import com.production.monoprix.util.SessionManager
import java.net.HttpURLConnection
import java.net.URL


class MyFirebaseMessagingService : FirebaseMessagingService() {

    private var notificationId: Int = 0
    private var resultPendingIntent: PendingIntent? = null
    val TAG = "FirebaseMessaging"
    var title: String? = ""
    var message: String? = ""
    var imgUrl: String? = ""
    var url: String? = ""
    var bitmap: Bitmap? = null
    lateinit var pendingIntent: PendingIntent
    lateinit var name: String
    lateinit var sessionManager: SessionManager


    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("LongLogTag")
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "23Nov: ${remoteMessage.from}")
        notificationId = System.currentTimeMillis().toInt()

        Log.e(TAG, "Data Payload: " + remoteMessage.getNotification())

        // Handle data payload of FCM messages.
        if (remoteMessage.data.isNotEmpty()) {
            // Handle the data message here.

            if (remoteMessage.data.isNotEmpty()) {
                try {
                    title = remoteMessage.data["title"]
                    message = remoteMessage.data["body"]
                    imgUrl = remoteMessage.data["image"]
                    url = remoteMessage.data["url"]
                    if (imgUrl.equals("null")) {
                        //nooo
                    } else
                        bitmap = getBitmapfromUrl(imgUrl!!)
                    showNotification()
                } catch (e: Exception) {
                    Log.e(TAG, "Exception: " + e.message)
                }

            }

        }

        // Handle notification payload of FCM messages.
        remoteMessage.notification?.let {
            // Handle the notification message here.

            try {
                title = remoteMessage.notification!!.title
                message = remoteMessage.notification!!.body
                imgUrl = remoteMessage.notification!!.imageUrl.toString()
                url = remoteMessage.notification!!.imageUrl.toString()
                if (imgUrl.equals("null")) {
                    //nooo
                } else
                    bitmap = getBitmapfromUrl(imgUrl!!)
                showNotification()
            } catch (e: Exception) {
                Log.e(TAG, "Exception: " + e.message)
            }


        }
/*
        if (isAppIsInBackground(applicationContext)) {

        }
*/

    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        sessionManager = SessionManager(this)
        sessionManager.fcmToken = p0
        Log.e(TAG, "Token perangkat ini: ${p0}")
        Log.d("23Nov:" + "NEW TOKENNN", p0)
    }

    private fun showNotification() {
        val e = Intent(applicationContext, SplashScreen::class.java)
        e.putExtra("notificationId", notificationId)
        e.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent =
                getActivity(this@MyFirebaseMessagingService, 0, e, PendingIntent.FLAG_IMMUTABLE)
        } else {
            pendingIntent =
                getActivity(this@MyFirebaseMessagingService, 0, e, PendingIntent.FLAG_ONE_SHOT)
        }
        if (imgUrl.equals("null")) {
            showNotification(this, title!!, message!!, e)

        } else {

            showNotificationImage(this, title!!, message!!, e)

        }

    }


    /*
    *To get a Bitmap image from the URL received
    * */
    fun getBitmapfromUrl(imageUrl: String): Bitmap? {
        try {
            val url = URL(imageUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.setDoInput(true)
            connection.connect()
            val input = connection.getInputStream()
            return BitmapFactory.decodeStream(input)

        } catch (e: Exception) {
            // TODO Auto-generated catch block
            e.printStackTrace()
            return null

        }

    }

    /**
     * Method checks if the app is in background or not
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    fun isAppIsInBackground(context: Context): Boolean {
        var isInBackground = true
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            val runningProcesses = am.runningAppProcesses
            for (processInfo in runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (activeProcess in processInfo.pkgList) {
                        if (activeProcess == context.packageName) {
                            isInBackground = false
                        }
                    }
                }
            }
        } else {
            val taskInfo = am.getRunningTasks(1)
            val componentInfo = taskInfo[0].topActivity
            if (componentInfo?.packageName == context.packageName) {
                isInBackground = false
            }
        }

        return isInBackground
    }

    fun showNotification(context: Context, title: String, body: String, intent: Intent) {

        val defaultSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

//      val notificationId = 1

        val channelId = "channel-01"
        val channelName = "Channel Name"
        val importance = NotificationManager.IMPORTANCE_HIGH

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(
                channelId, channelName, importance
            )
            notificationManager.createNotificationChannel(mChannel)
        }

        val mBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.withoutbg)//R.mipmap.ic_app
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(defaultSound)
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setWhen(System.currentTimeMillis())
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setColorized(true) // Allow for customizing the color
            .setColor(Color.RED) // Set the color of the notification

        val stackBuilder = TaskStackBuilder.create(context)
        stackBuilder.addNextIntent(intent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        mBuilder.setContentIntent(resultPendingIntent)

        notificationManager.notify(notificationId, mBuilder.build())

    }

    fun showNotificationImage(context: Context, title: String, body: String, intent: Intent) {
        val defaultSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "channel-01"
        val channelName = "Channel Name"
        val importance = NotificationManager.IMPORTANCE_HIGH

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(
                channelId, channelName, importance
            )
            mChannel.setShowBadge(true)
            notificationManager.createNotificationChannel(mChannel)

        }

        val mBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.withoutbg)//R.mipmap.ic_app
            .setContentTitle(title)
            .setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(defaultSound)
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setWhen(System.currentTimeMillis())
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setColorized(true) // Allow for customizing the color
            .setColor(Color.RED) // Set the color of the notification

        val stackBuilder = TaskStackBuilder.create(context)
        stackBuilder.addNextIntent(intent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }


        mBuilder.setContentIntent(resultPendingIntent)

        notificationManager.notify(notificationId, mBuilder.build())
    }


}