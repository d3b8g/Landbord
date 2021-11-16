package net.d3b8g.landbord.ui.notifications

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.d3b8g.landbord.R

class NotificationsAdapter(private val notificationsList: NotificationsAdapterModels) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.cell_notification, parent, false)
        return NotificationsAdapterHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder , position: Int) {
        if (holder is NotificationsAdapterHolder)
            holder.bind(notificationsList.notificationsList[position])
    }

    override fun getItemCount(): Int = notificationsList.notificationsList.size

    class NotificationsAdapterHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: NotificationsAdapterModel) {

        }
    }
}