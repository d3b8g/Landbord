package net.d3b8g.landbord.ui.widgets.statistic

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.d3b8g.landbord.database.Booking.BookingDatabaseDao
import net.d3b8g.landbord.database.Checklists.CheckListDatabaseDao

class StatisticViewModelFactory(
    private val dataSource: BookingDatabaseDao,
    private val dataSourceCL: CheckListDatabaseDao,
    private val application: Application
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST") //give a shit
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatisticViewModel::class.java)) {
            return StatisticViewModel(dataSource, dataSourceCL, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}