package net.d3b8g.landbord.ui.add

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AddViewModel : ViewModel() {

    val state = MutableLiveData<AddViewState>()

    val tabbarHide = MutableLiveData<Boolean>(false)
}