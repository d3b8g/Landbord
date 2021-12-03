package net.d3b8g.landbord.ui.widgets.statistic

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
import net.d3b8g.landbord.database.Checklists.CheckListData
import net.d3b8g.landbord.database.Checklists.CheckListDatabaseDao
import java.util.*

class StatisticViewModel(private val database: BookingDatabaseDao, databaseCheckList: CheckListDatabaseDao, application: Application) : AndroidViewModel(application) {

    private val yearMonth = "${parseDateToModel(getTodayDate()).year}-${parseDateToModel(getTodayDate()).month}"
    private val startDate = "${yearMonth}-01"
    private val endDate = "${yearMonth}-${Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)}"

    @SuppressLint("SimpleDateFormat")
    val statisticsBookingData = MutableLiveData<List<BookingData>>().apply {

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                postValue(database.getListByDate(startDate, endDate))
            }
        }
    }

    val statisticsBuyItems = MutableLiveData<List<CheckListData>>().apply {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                postValue(databaseCheckList.getThisMonthItems(startDate, endDate))
            }
        }
    }
}