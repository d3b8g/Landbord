package net.d3b8g.landbord.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.d3b8g.landbord.database.Booking.BookingData
import net.d3b8g.landbord.database.Booking.BookingDatabaseDao
import net.d3b8g.landbord.database.Flat.FlatData
import net.d3b8g.landbord.database.Flat.FlatDatabaseDao
import net.d3b8g.landbord.ui.home.HomeFragment.Companion.FLAT_LAST_KEY

class HomeViewModel(private val databaseFlat: FlatDatabaseDao,
                    private val databaseBooking: BookingDatabaseDao,
                    application: Application) : AndroidViewModel(application) {

    var flat = MutableLiveData<FlatData?>().apply {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                PreferenceManager.getDefaultSharedPreferences(application).apply {
                    postValue(databaseFlat.get(getInt(FLAT_LAST_KEY, 1)))
                }
            }
        }
    }

    var flatList = MutableLiveData<List<String>>().apply {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                postValue(databaseFlat.getAllFlatsTitle())
            }
        }
    }


    fun getBookingData(date: String) = MutableLiveData<BookingData>().apply {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                postValue(databaseBooking.getByDate(date))
            }
        }
    }
}