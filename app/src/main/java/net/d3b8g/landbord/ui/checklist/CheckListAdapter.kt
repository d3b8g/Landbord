package net.d3b8g.landbord.ui.checklist

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.d3b8g.landbord.R
import net.d3b8g.landbord.components.Converter
import net.d3b8g.landbord.components.Converter.parseDateToModel
import net.d3b8g.landbord.database.Checklists.CheckListData
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CheckListAdapter(val aboba: CheckListInterface) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val checkListItemArray: ArrayList<CheckListData> = ArrayList()

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(dataList: ArrayList<CheckListData>): Boolean {
        checkListItemArray.clear()
        checkListItemArray.addAll(dataList)
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

        @SuppressLint("SetTextI18n", "SimpleDateFormat")
        fun bind(item: CheckListData) {
            val checkBox = itemView.findViewById<CheckBox>(R.id.check_list_check_box)
            val textOfRepeat = itemView.findViewById<TextView>(R.id.check_list_when_repeat)
            val changeRepeat = itemView.findViewById<Button>(R.id.check_list_change_repeat)

            checkBox.apply {
                text = item.title
                isChecked = item.isCompleted

                setOnClickListener {
                    if (checkBox.isChecked) {

                    }
                }
            }

            changeRepeat.setOnClickListener {
                aboba.openAddItemModalView(CheckListModalViewStates.EDIT_ITEM, item.id)
            }

            val parseReminderDate = parseDateToModel(item.reminderDate)
            val dateReminder = if (parseReminderDate.year == SimpleDateFormat("yyyy").format(Date())) {
                Converter.convertDate(parseReminderDate.month.toInt(), parseReminderDate.day.toInt())
            } else item.reminderDate

            textOfRepeat.text = "${itemView.context.getString(R.string.check_list_reminder_to_buy)}: $dateReminder"
        }
    }
}