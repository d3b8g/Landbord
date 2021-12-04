package net.d3b8g.landbord.ui.widgets.add_info

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.d3b8g.landbord.database.Booking.BookingData

class AddInfoViewModel : ViewModel() {

    val widgetSetState = MutableLiveData<Boolean>()

    val chosenCalendarDate = MutableLiveData<String>()

    val shouldUpdateWidget = MutableLiveData<BookingData>()
}