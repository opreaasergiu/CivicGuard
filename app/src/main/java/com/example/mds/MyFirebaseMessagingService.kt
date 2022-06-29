package com.example.mds

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Build.VERSION_CODES
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

const val channelId = "notification_channel"
const val channelName = "com.example.mds"

class MyFirebaseMessagingService : FirebaseMessagingService(){

//    private lateinit var mAuth: FirebaseAuth
//
//    override fun onNewToken(token: String) {
//        Log.d("Token: ", "Refreshed token: $token")
//
//        mAuth = FirebaseAuth.getInstance()
//        val user = mAuth.currentUser
//        FirebaseDatabase.getInstance().getReference("Users").child(user?.uid.toString()).child("deviceToken").setValue(token).addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//
//                Log.w("User ", user?.uid.toString())
//            } else {
//                Log.w("User ", "Failed ")
//            }
//        }
//
//
//    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if(remoteMessage.getNotification() != null)
        {
            generateNotification(remoteMessage.notification!!.title!!, remoteMessage.notification!!.body!!)

        }
    }
    fun getRemoteView(title: String, message: String):RemoteViews{
        val remoteView = RemoteViews("com.example.mds", R.layout.notification)

        remoteView.setTextViewText(R.id.notTitle,title)
        remoteView.setTextViewText(R.id.message,message)
        remoteView.setImageViewResource(R.id.app_logo,R.drawable.image)

        return remoteView
    }
    fun generateNotification(title: String, message: String)
    {
        val intent = Intent(this,MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val flags = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE
            else -> FLAG_UPDATE_CURRENT
        }
        val pendingIntent = PendingIntent.getActivity(this,0,intent,flags)//PendingIntent.FLAG_ONE_SHOT)

        var builder: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.image)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000,1000,1000,1000))
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)

        builder = builder.setContent(getRemoteView(title,message))

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            val notificationChannel = NotificationChannel(channelId, channelName,NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        notificationManager.notify(0,builder.build())

    }
}