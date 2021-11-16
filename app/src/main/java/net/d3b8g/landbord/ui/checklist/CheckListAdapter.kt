package net.d3b8g.landbord.ui.checklist

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import net.d3b8g.landbord.R
import net.d3b8g.landbord.models.CheckListItemModel

class CheckListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val checkListItemArray: ArrayList<CheckListItemModel> = ArrayList()
    @SuppressLint("NotifyDataSetChanged")
    fun updateList(dataList: ArrayList<CheckListItemModel>) {
        if (checkListItemArray.isNotEmpty()) {
            checkListItemArray.forEachIndexed { index, checkListItemModel ->
                if (checkListItemModel != dataList[index]) {
                    checkListItemArray[index] = dataList[index]
                    notifyItemChanged(index)
                }
            }
        } else {
            checkListItemArray.addAll(dataList)
            Log.e("RRR", "aboab $checkListItemArray")
            notifyDataSetChanged()
        }
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

    class CheckListAdapterHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bind(item: CheckListItemModel) {
            val checkBox = itemView.findViewById<CheckBox>(R.id.check_list_check_box)
            val title = itemView.findViewById<TextView>(R.id.check_list_title)
            val isRepeatable = itemView.findViewById<ImageButton>(R.id.check_list_repeat)

            val blockWithRepeatInfo = itemView.findViewById<LinearLayout>(R.id.check_list_item_next_repeat)
            val textOfRepeat = itemView.findViewById<TextView>(R.id.check_list_when_repeat)
            val changeRepeat = itemView.findViewById<Button>(R.id.check_list_change_repeat)

            title.text = item.title
            checkBox.isChecked = item.isCompleted

            checkBox.setOnClickListener {
                if (checkBox.isChecked && item.isRepeatable) {
                    blockWithRepeatInfo.visibility = View.VISIBLE
                    textOfRepeat.text = itemView.context.getString(R.string.check_list_next_repeat) + item.nextRepeat
                }
            }
        }
    }
}