package com.giniapps.mylocation4.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.giniapps.weatherapplication.R
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject

class NotificationGeneratorImpl @Inject constructor(@ApplicationContext val context: Context) :
    NotificationGenerator {
    private val NOTIFICATION_CHANNEL_ID = UUID.randomUUID().toString()

    //an object that sends the notifications:
    private val notificationManager: NotificationManager by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun getNotificationCompatBuilder(
        id:Int,
        title: String,
        content: String,
        isOngoing:Boolean,
        intent:Intent
    ): NotificationCompat.Builder {

        // 1. Create Notification Channel for O+ and beyond devices (26+).
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID, title, NotificationManager.IMPORTANCE_DEFAULT
            )
            // Adds NotificationChannel to system. Attempting to create an
            // existing notification channel with its original values performs
            // no operation, so it's safe to perform the below sequence.
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager



        // 2. Build the BIG_TEXT_STYLE.
        val bigTextStyle = NotificationCompat.BigTextStyle()
            .bigText(content)
            .setBigContentTitle(title)

        // 3. Set up main Intent/Pending Intents for notification.
        intent.apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 4. Build and issue the notification.
        // Notification Channel Id is ignored for Android pre O (26).
        val notificationCompatBuilder =
            NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)

        return notificationCompatBuilder
            .setStyle(bigTextStyle)
            .setContentIntent(pendingIntent)
            .setContentTitle(title)
            .setOnlyAlertOnce(true) // so when data is updated don't make sound and alert in android 8.0+
            .setContentText(content)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setOngoing(isOngoing)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
    }
}