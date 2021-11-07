package net.d3b8g.landbord.widgets.booking

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.d3b8g.landbord.models.BookingInfoModel

class BookingInfoViewModel : ViewModel() {

    val widgetModel = MutableLiveData<BookingInfoModel>()

    val deleteUserBooking = MutableLiveData<Boolean>()

}