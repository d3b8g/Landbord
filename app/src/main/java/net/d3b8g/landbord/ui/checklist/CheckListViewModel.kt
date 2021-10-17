package net.d3b8g.landbord.ui.checklist

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import net.d3b8g.landbord.models.CheckListItemModel
import org.json.JSONArray

class CheckListViewModel : ViewModel() {

    fun checkList(context: Context) = MutableLiveData<ArrayList<CheckListItemModel>>().apply {
        PreferenceManager.getDefaultSharedPreferences(context).apply {
            val checkList = JSONArray(getString("check_list_prefs", "[]"))
            val arrayBack: ArrayList<CheckListItemModel> = ArrayList()
            for (i in 0 until checkList.length()) {
                arrayBack.add(CheckListItemModel(
                    title = checkList.getJSONObject(i).getString("title"),
                    isCompleted = checkList.getJSONObject(i).getBoolean("isCompleted"),
                    isRepeatable = checkList.getJSONObject(i).getBoolean("isRepeatable"),
                    nextRepeat = checkList.getJSONObject(i).getString("nextRepeat")
                ))
            }
            postValue(arrayBack)
        }
    }
}