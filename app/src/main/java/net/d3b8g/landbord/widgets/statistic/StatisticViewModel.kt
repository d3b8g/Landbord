package net.d3b8g.landbord.widgets.statistic

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.d3b8g.landbord.components.Converter.getTodayDate
import net.d3b8g.landbord.components.Converter.parseDateToModel
import net.d3b8g.landbord.database.Booking.BookingData
import net.d3b8g.landbord.database.Booking.BookingDatabaseDao
import java.util.*

class StatisticViewModel(private val database: BookingDatabaseDao, application: Application) : AndroidViewModel(application) {

    @SuppressLint("SimpleDateFormat")
    val statisticsBookingData = MutableLiveData<List<BookingData>>().apply {

        val yearMonth = "${parseDateToModel(getTodayDate()).year}-${parseDateToModel(getTodayDate()).month}"
        val startDate = "${yearMonth}-01"
        val endDate = "${yearMonth}-${Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)}"

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                postValue(database.getListByDate(startDate, endDate))
            }
        }
    }

}