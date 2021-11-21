package net.d3b8g.landbord.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.d3b8g.landbord.components.Converter.getTodayDate
import net.d3b8g.landbord.components.Converter.getTodayFullTime
import net.d3b8g.landbord.components.setNotificationsJson
import net.d3b8g.landbord.database.Booking.BookingDatabase
import net.d3b8g.landbord.notification.NotificationHelper.createNotification
import net.d3b8g.landbord.ui.notifications.NotificationsAdapterModel
import net.d3b8g.landbord.ui.notifications.NotificationsDelayType

class NotificationReceiver : BroadcastReceiver() {

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onReceive(ct: Context , p1: Intent?) {
        val dbBooking = BookingDatabase.getInstance(ct).bookedDatabaseDao
        val today = getTodayDate()
        val fullDate = getTodayFullTime()

        scope.launch {
            val data = dbBooking.getListByMonth("${today.dropLast(2)}01",
                "${today.dropLast(2)}31")

            if (data.isNullOrEmpty()) {
                ct.createNotification()
                setNotificationsJson(ct, NotificationsAdapterModel(
                    id = 0,
                    type = NotificationsDelayType.EMPTY_DATA,
                    date = fullDate
                ))
            }
        }
    }
}