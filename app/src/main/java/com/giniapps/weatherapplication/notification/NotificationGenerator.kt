package com.giniapps.mylocation4.notification

import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

interface NotificationGenerator {
    fun getNotificationCompatBuilder(id:Int,title:String,content:String,isOngoing:Boolean,intent: Intent): NotificationCompat.Builder
}