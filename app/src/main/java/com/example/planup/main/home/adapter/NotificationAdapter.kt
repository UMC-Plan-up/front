package com.example.planup.main.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.planup.R
import com.example.planup.network.dto.notification.NotificationResult
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil

class NotificationAdapter :
    ListAdapter<NotificationResult, NotificationAdapter.ViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val text = itemView.findViewById<TextView>(R.id.tvNotificationText)
        private val time = itemView.findViewById<TextView>(R.id.tvTime)

        fun bind(item: NotificationResult) {
            text.text = item.notificationText
            time.text = item.createdAt // 필요하면 포맷팅
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<NotificationResult>() {
            override fun areItemsTheSame(
                oldItem: NotificationResult,
                newItem: NotificationResult
            ) = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: NotificationResult,
                newItem: NotificationResult
            ) = oldItem == newItem
        }
    }
}
