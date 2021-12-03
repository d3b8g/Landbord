package net.d3b8g.landbord.notification

import android.app.*
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import net.d3b8g.landbord.MainActivity
import net.d3b8g.landbord.R
import net.d3b8g.landbord.components.HelperComponents
import net.d3b8g.landbord.components.getNotificationDelay
import net.d3b8g.landbord.components.getNotificationStatus
import net.d3b8g.landbord.database.Booking.BookingData
import net.d3b8g.landbord.database.Checklists.CheckListData
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

    fun Context.createNotification(data: List<Any>?) {
        val notificationChannelId = "1350"

        val builder: Notification.Builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Notification.Builder(
            this,
            notificationChannelId
        ) else Notification.Builder(this)

        val pendingIntent: PendingIntent = Intent(this, MainActivity::class.java).let { notificationIntent ->
            PendingIntent.getActivity(this, 0, notificationIntent, pendingIntentFlag)
        }

        var notificationContentText = ""
        var notificationContentTitle = ""

        if (data != null && data[0]::class.java.simpleName == BookingData::class.java.simpleName) {

            val openCorrectBookingSnooze = Intent(this, MainActivity::class.java).apply {
                action = "CALL_BODY"
                putExtra("BOOKING_ID", (data[0] as BookingData).id)
            }
            val pendingIntentSnooze =
                PendingIntent.getBroadcast(this, 0, openCorrectBookingSnooze, pendingIntentFlag)

            val notificationAction = NotificationCompat.Action.Builder(
                R.drawable.ic_contact_phone_24,
                getString(R.string.call),
                pendingIntentSnooze
            ) as Notification.Action


            notificationContentTitle = getString(R.string.notification_have_tenant)
            (data[0] as BookingData).also {
                notificationContentText = if (it.deposit > 0)
                 "${it.username} ${getString(R.string.notification_reserved)}"
                 else "${it.username} ${getString(R.string.notification_reserved_with_deposit)} ${it.deposit}"
            }

            builder
                .setContentTitle(notificationContentTitle)
                .setContentText(notificationContentText)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(Notification.PRIORITY_HIGH) // for under android 26 compatibility
                .addAction(notificationAction)
                .build()
        } else {
            if (data != null && data[0]::class.java.simpleName == CheckListData::class.java.simpleName) {
                notificationContentText = "${getString(R.string.notification_should_buy)} " +
                        (data as List<CheckListData>).joinToString(", ") { it.title }
                notificationContentTitle = getString(R.string.reminder)

            } else {
                notificationContentText = getString(R.string.notification_havent_today_record)
                notificationContentTitle = getString(R.string.notification_add_new_calendar)
            }

            builder
                .setContentTitle(notificationContentTitle)
                .setContentText(notificationContentText)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(Notification.PRIORITY_HIGH) // for under android 26 compatibility
                .build()
        }

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
        notificationManager.notify(HelperComponents.randomInt(), builder.build())
    }
}