package net.d3b8g.landbord.components

import android.annotation.SuppressLint
import net.d3b8g.landbord.components.Converter.covertStringToDate
import net.d3b8g.landbord.database.Booking.BookingData
import java.text.SimpleDateFormat
import java.util.*

object DateHelper {

    //const val dayInMlSeconds = 86400000

    @SuppressLint("SimpleDateFormat")
    fun getTriggerTime(currentHour: Int, currentMinutes: Int, getDiff: Boolean): Long {
        val nowTime: List<Int> = SimpleDateFormat("HH:mm").format(Date()).split(':').map { it.toInt() }
        val targetTime = Calendar.getInstance().run {
            if (nowTime[0] >= currentHour && nowTime[1] >= currentMinutes) {
                add(Calendar.DATE, 1)
            }
            set(Calendar.HOUR, currentHour)
            set(Calendar.MINUTE, currentMinutes)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            timeInMillis
        }

        return if (getDiff) {
            targetTime
        } else {
            val nowInMills = Calendar.getInstance().timeInMillis
            targetTime - nowInMills
        }
    }

    fun getCloserDate(dataArray: List<BookingData>, closerDate: Date): Int? {
        for (i in dataArray) {
            val dateStart = i.bookingDate.covertStringToDate()
            val dateEnd = i.bookingEnd.covertStringToDate()

            if ((closerDate.after(dateStart) && closerDate.before(dateEnd)) ||
                (closerDate == dateStart || closerDate == dateEnd)) return i.id
        }
        return null
    }
}