package net.d3b8g.landbord.widgets.statistic

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.d3b8g.landbord.database.Booking.BookingData
import net.d3b8g.landbord.database.Booking.BookingDatabaseDao
import java.text.SimpleDateFormat
import java.util.*

class StatisticViewModel(private val database: BookingDatabaseDao, application: Application) : AndroidViewModel(application) {

    @SuppressLint("SimpleDateFormat")
    var statisticsBookingData = MutableLiveData<List<BookingData>>().apply {
        val getMonth = SimpleDateFormat("yyyy-MM").format(Date())
        val startDate = SimpleDateFormat("yyyy-MM-dd").parse("${getMonth}-01")!!
        val endDate = SimpleDateFormat("yyyy-MM-dd").parse("${getMonth}-${Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)}")!!

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                postValue(database.getListByDate(startDate, endDate))
            }
        }
    }

}