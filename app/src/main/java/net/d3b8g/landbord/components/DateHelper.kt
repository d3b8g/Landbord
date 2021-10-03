package net.d3b8g.landbord.components

import android.util.Log
import net.d3b8g.landbord.cache.AppCache.findDateCache
import net.d3b8g.landbord.components.Converter.convertUnixToDate
import net.d3b8g.landbord.components.Converter.covertStringToDate
import net.d3b8g.landbord.components.Converter.parseDateToModel
import net.d3b8g.landbord.database.Booking.BookingData
import java.util.*

object DateHelper {

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