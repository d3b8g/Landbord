package net.d3b8g.landbord.notification

import android.app.*
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import net.d3b8g.landbord.MainActivity
import net.d3b8g.landbord.R
import net.d3b8g.landbord.components.getNotificationDelay
import net.d3b8g.landbord.components.getNotificationStatus
import java.util.*

object NotificationHelper {

    private val pendingIntentFlag = if (Build.VERSION.SDK_INT > 30) PendingIntent.FLAG_MUTABLE else PendingIntent.FLAG_UPDATE_CURRENT

    fun Context.delayedNotificationAlarm() {

        val setupTime = getNotificationDelay(this).split(':').map { it.toInt() }
        val doRepeat = getNotificationStatus(this)

        val calendar = Calendar.getInstance().run {
            set(Calendar.HOUR_OF_DAY, setupTime[0])
            set(Calendar.MINUTE, setupTime[1])
            set(Calendar.SECOND, 1)
            if (timeInMillis < Date().time) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
            timeInMillis
        }

        val intentNotification = Intent(this, NotificationReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(this, 0, intentNotification, pendingIntentFlag)
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        if (doRepeat) {
            alarmManager?.let {
                it.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )
            }
        }
    }

    fun Context.createNotification() {
        val notificationChannelId = "1350"

        val builder: Notification.Builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Notification.Builder(
            this,
            notificationChannelId
        ) else Notification.Builder(this)

        Log.e("Notification", "createNotif")

        val pendingIntent: PendingIntent = Intent(this, MainActivity::class.java).let { notificationIntent ->
            PendingIntent.getActivity(this, 0, notificationIntent, pendingIntentFlag)
        }

        builder
            .setContentTitle("Добавьте информацию в календарь")
            .setContentText("В вашем календаре еще не добавлена информация о новых жильцах.")
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setTicker("Ticker text")
            .setPriority(Notification.PRIORITY_HIGH) // for under android 26 compatibility
            .build()

        // depending on the Android API that we're dealing with we will have
        // to use a specific method to create the notification
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                notificationChannelId ,
                "Endless Service notifications channel" ,
                NotificationManager.IMPORTANCE_DEFAULT
            ).let {
                it.description = "Endless Service channel"
                it.enableLights(true)
                it.lightColor = Color.RED
                it.enableVibration(true)
                it.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                it.vibrationPattern =
                    longArrayOf(100 , 200 , 300 , 400 , 500 , 400 , 300 , 200 , 400)
                it
            }
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(1, builder.build())
    }
}