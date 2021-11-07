package net.d3b8g.landbord.notification

import android.annotation.SuppressLint
import android.app.*
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import kotlinx.coroutines.*
import net.d3b8g.landbord.MainActivity
import net.d3b8g.landbord.R
import net.d3b8g.landbord.components.DateHelper.getTriggerTime
import net.d3b8g.landbord.database.Booking.BookingDatabase
import java.text.SimpleDateFormat
import java.util.*

class NotificationService: Service() {

    private var wakeLock: PowerManager.WakeLock? = null
    private var isServiceStarted = false
    private val currentTime by lazy {
        getNotificationDelay(this)
    }
    private val dbBooking by lazy { initBooking() }

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    override fun onStartCommand(intent: Intent?, flags: Int , startId: Int): Int {
        Log.e("Notification", "startCommand")
        intent?.let {
            when (it.action) {
                NotificationsActions.START.name -> startService()
                NotificationsActions.STOP.name -> stopService()
            }
        }
        return START_NOT_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        startForeground(1, createNotification())
    }

    @SuppressLint("SimpleDateFormat")
    private fun startService() {
        Log.e("Notification", "startService")
        if (isServiceStarted) return
        //Toast.makeText(this, "Service starting its task", Toast.LENGTH_SHORT).show()
        isServiceStarted = true
        setServiceState(this, ServiceState.STARTED)

        // we need this lock so our service gets not affected by Doze Mode
        wakeLock = (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
            newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "NotificationService::lock").apply {
                acquire()
            }
        }

        val triggerTimePrefs = currentTime.split(':').map { it.toInt() }
        val triggerTime = getTriggerTime(triggerTimePrefs[0], triggerTimePrefs[1], false)
        scope.launch {
            while (isServiceStarted) {
                delay(60 * 1000)
                launch(Dispatchers.IO) {
                    val timeAt = SimpleDateFormat("yyyy-MM-dd").format(Date())
                    val data = dbBooking.getListByMonth("${timeAt.dropLast(2)}01","${timeAt.dropLast(2)}31")
                    if (data != null) {

                    }
                }
            }
        }
    }

    private fun stopService() {
        try {
            wakeLock?.let {
                if (it.isHeld) {
                    it.release()
                }
            }
            stopForeground(true)
            stopSelf()
        } catch (e: Exception) {
            Log.e("Notification", "Service stopped without being started: ${e.message}")
        }
        isServiceStarted = false
        setServiceState(this, ServiceState.STOPPED)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotification(): Notification {
        val notificationChannelId = "1350"

        // depending on the Android API that we're dealing with we will have
        // to use a specific method to create the notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                notificationChannelId,
                "Endless Service notifications channel",
                NotificationManager.IMPORTANCE_DEFAULT
            ).let {
                it.description = "Endless Service channel"
                it.enableLights(true)
                it.lightColor = Color.RED
                it.enableVibration(true)
                it.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                it.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
                it
            }
            notificationManager.createNotificationChannel(channel)
        }

        val pendingIntent: PendingIntent = Intent(this, MainActivity::class.java).let { notificationIntent ->
            PendingIntent.getActivity(this, 0, notificationIntent, FLAG_IMMUTABLE)
        }

        val builder: Notification.Builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Notification.Builder(
            this,
            notificationChannelId
        ) else Notification.Builder(this)

        Log.e("Notification", "createNotif")

        return builder
            .setContentTitle("Добавьте информацию в календарь ")
            .setContentText("В вашем календаре еще не добавлена информация о новых жильцах.")
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setTicker("Ticker text")
            .setPriority(Notification.PRIORITY_HIGH) // for under android 26 compatibility
            .build()
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        val restartServiceIntent = Intent(applicationContext, NotificationService::class.java).also {
            it.setPackage(packageName)
        }
        val restartServicePendingIntent: PendingIntent =
            PendingIntent.getService(this, 1, restartServiceIntent, FLAG_IMMUTABLE)
        applicationContext.getSystemService(Context.ALARM_SERVICE)

        val triggerTimePrefs = currentTime.split(':').map { it.toInt() }
        val triggerTime = getTriggerTime(triggerTimePrefs[0], triggerTimePrefs[1], true)
        Log.e("Notification", "On restart task and send at $triggerTime")

        val alarmService: AlarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmService.set(AlarmManager.ELAPSED_REALTIME, triggerTime, restartServicePendingIntent)
    }

    private fun initBooking() = BookingDatabase.getInstance(this).bookedDatabaseDao
}