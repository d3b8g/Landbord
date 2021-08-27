package net.d3b8g.landbord.ui.home

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.d3b8g.landbord.database.Booking.BookingDatabaseDao
import net.d3b8g.landbord.database.Flat.FlatDatabaseDao

class HomeViewModelFactory(
    private val dataSource: FlatDatabaseDao,
    private val dataSourceBooking: BookingDatabaseDao,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(dataSource, dataSourceBooking, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}