package net.d3b8g.landbord.ui.income

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class IncomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is income Fragment"
    }
    val text: LiveData<String> = _text
}