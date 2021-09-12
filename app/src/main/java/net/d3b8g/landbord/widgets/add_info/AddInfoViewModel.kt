package net.d3b8g.landbord.widgets.add_info

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AddInfoViewModel : ViewModel() {

    val widgetSetState = MutableLiveData<Boolean>()

    val chosenCalendarDate = MutableLiveData<String>()

    val shouldUpdateWidget = MutableLiveData<Boolean>()
}