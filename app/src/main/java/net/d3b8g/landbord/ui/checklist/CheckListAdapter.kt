package net.d3b8g.landbord.ui.checklist

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.d3b8g.landbord.R
import net.d3b8g.landbord.components.Converter
import net.d3b8g.landbord.components.Converter.convertStringToDate
import net.d3b8g.landbord.components.Converter.parseDateToModel
import net.d3b8g.landbord.database.Checklists.CheckListData
import net.d3b8g.landbord.database.Checklists.CheckListDatabase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CheckListAdapter(val checkListInterface: CheckListInterface) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val checkListItemArray: ArrayList<CheckListData> = ArrayList()

    @SuppressLint("NotifyDataSetChanged", "SimpleDateFormat")
    fun updateList(dataList: ArrayList<CheckListData>): Boolean {
        checkListItemArray.clear()

        //do sort data by date
        val sortedDataList = dataList.sortedBy { it.reminderDate.convertStringToDate() }
        checkListItemArray.addAll(sortedDataList)

        notifyDataSetChanged()
        return checkListItemArray.size > 0
    }

    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.cell_check_list_item, parent, false)
        return CheckListAdapterHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder , position: Int) {
        if (holder is CheckListAdapterHolder) holder.bind(checkListItemArray[position])
    }

    override fun getItemCount(): Int = checkListItemArray.size

    inner class CheckListAdapterHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private lateinit var checkBox: CheckBox
        private lateinit var checkListItem: CheckListData
        private val scope = CoroutineScope(Dispatchers.IO)

        @SuppressLint("SetTextI18n", "SimpleDateFormat")
        fun bind(item: CheckListData) {
            checkBox = itemView.findViewById(R.id.check_list_check_box)
            val textOfRepeat = itemView.findViewById<TextView>(R.id.check_list_when_repeat)
            val changeRepeat = itemView.findViewById<Button>(R.id.check_list_change_repeat)

            checkBox.apply {
                text = item.title
                isChecked = item.isCompleted

                setOnClickListener {
                    if (checkBox.isChecked) {
                        val nextRemindDate = Calendar.getInstance().run {
                            add(Calendar.DATE, 30)
                            time
                        }
                        checkListItem = CheckListData(
                            id = item.id,
                            title = item.title,
                            isCompleted = item.isCompleted,
                            isRepeatable = item.isRepeatable,
                            reminderDate = SimpleDateFormat("yyyy-MM-dd").format(nextRemindDate)
                        )
                        showPopupBought()
                    }
                }
            }

            changeRepeat.setOnClickListener {
                checkListInterface.openAddItemModalView(CheckListModalViewStates.EDIT_ITEM, item.id)
            }

            val parseReminderDate = parseDateToModel(item.reminderDate)
            val dateReminder = if (parseReminderDate.year == SimpleDateFormat("yyyy").format(Date())) {
                Converter.convertDate(parseReminderDate.month.toInt(), parseReminderDate.day.toInt())
            } else item.reminderDate

            textOfRepeat.text = "${itemView.context.getString(R.string.check_list_reminder_to_buy)}: $dateReminder"
        }

        private fun showPopupBought() {
            MaterialAlertDialogBuilder(itemView.context)
                .setTitle(R.string.bought_this)
                .setMessage(R.string.bought_message)
                .setPositiveButton(R.string.yes) { dialog, _ ->
                    scope.launch { updateCheckListItem() }
                    dialog.dismiss()
                }
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    checkBox.isChecked = false
                    dialog.dismiss()
                }
                .show()
        }

        private fun updateCheckListItem() {
            val checkListDatabase = CheckListDatabase.getInstance(itemView.context).checkListDatabaseDao

            if (this::checkListItem.isInitialized) {
                checkListDatabase.update(checkListItem)
                checkListInterface.updateRecyclerViewList()
            }
        }
    }
}