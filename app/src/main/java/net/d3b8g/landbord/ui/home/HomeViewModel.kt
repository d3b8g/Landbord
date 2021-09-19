package net.d3b8g.landbord.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.d3b8g.landbord.database.Booking.BookingData
import net.d3b8g.landbord.database.Booking.BookingDatabaseDao
import net.d3b8g.landbord.database.Flat.FlatDatabaseDao

class HomeViewModel(private val databaseFlat: FlatDatabaseDao,
                    private val databaseBooking: BookingDatabaseDao,
                    application: Application) : AndroidViewModel(application) {

    val flatList = MutableLiveData<List<String>>().apply {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                postValue(databaseFlat.getAllFlatsTitle())
            }
        }
    }

    fun getBookingData(date: String) = MutableLiveData<BookingData>().apply {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (databaseBooking.getListByDate(date, date)?.isNotEmpty() == true)
                    postValue(databaseBooking.getListByDate(date, date)!![0])
            }
        }
    }
}