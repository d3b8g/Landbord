package net.d3b8g.landbord.ui.home

import android.app.Application
import android.view.View
import androidx.lifecycle.*
import androidx.preference.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.d3b8g.landbord.database.FlatData
import net.d3b8g.landbord.database.FlatDatabase
import net.d3b8g.landbord.database.FlatDatabaseDao
import net.d3b8g.landbord.ui.home.HomeFragment.Companion.FLAT_LAST_KEY

class HomeViewModel(private val database: FlatDatabaseDao, application: Application) : AndroidViewModel(application) {

    private val _flat = MutableLiveData<FlatData?>().apply {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                postValue(database.get(1))
            }
        }
    }

    var flat = _flat

}