package net.d3b8g.landbord.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.d3b8g.landbord.cache.AppCache
import net.d3b8g.landbord.cache.AppCache.dateCache
import net.d3b8g.landbord.cache.AppCache.findDateCache
import net.d3b8g.landbord.cache.AppCache.haveDateCache
import net.d3b8g.landbord.components.Converter.convertStringToDate
import net.d3b8g.landbord.components.DateHelper.getCloserDate
import net.d3b8g.landbord.database.Booking.BookingData
import net.d3b8g.landbord.database.Booking.BookingDatabaseDao
import net.d3b8g.landbord.database.Flat.FlatDatabaseDao
import java.util.*

class HomeViewModel(private val databaseFlat: FlatDatabaseDao,
                    private val databaseBooking: BookingDatabaseDao,
                    application: Application) : AndroidViewModel(application) {

    val flatList = MutableLiveData<List<String>>().apply {
        viewModelScope.launch(Dispatchers.IO) {
            postValue(databaseFlat.getAllFlatsTitle())
        }
    }

    val haveBooking = MutableLiveData<Boolean>().apply {
        viewModelScope.launch(Dispatchers.IO) {
            postValue(databaseBooking.countBooking() > 0)
        }
    }

    fun getBookingData(date: String) = MutableLiveData<BookingData>().apply {
        viewModelScope.launch(Dispatchers.IO) {
            if (dateCache.isNotEmpty() && date.convertStringToDate().haveDateCache()) {
                val idWithDate = date.convertStringToDate().findDateCache()
                val getDataWithId = databaseBooking.getById(idWithDate)
                postValue(getDataWithId)
            } else {
                val data = databaseBooking.getListByMonth("${date.dropLast(2)}01","${date.dropLast(2)}31")
                if (!data.isNullOrEmpty()) {
                    val closerDateId = getCloserDate(data, date.convertStringToDate())
                    closerDateId?.let {
                        val closerDateData = databaseBooking.getById(closerDateId)
                        dateCache.add(AppCache.DataCache(
                            dateStart = closerDateData.bookingDate.convertStringToDate(),
                            dateEnd = closerDateData.bookingEnd.convertStringToDate(),
                            uid = closerDateData.id
                        ))
                        postValue(closerDateData)
                        //Should to close thread
                        return@launch
                    }
                    //We should return NULL for UI reaction
                    postValue(null)
                } else {
                    postValue(null)
                }
            }
        }
    }
}