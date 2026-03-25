package com.planup.planup.main.home.adapter

import android.R.attr.onClick
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.planup.planup.R
import com.planup.planup.network.dto.notification.NotificationResult

class NotificationAdapter(
    private val onClick: (NotificationResult) -> Unit
) :
    ListAdapter<NotificationResult, NotificationAdapter.ViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home_alert, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)) { item ->
            onClick(item)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val text = itemView.findViewById<TextView>(R.id.tvNotificationText)
        private val time = itemView.findViewById<TextView>(R.id.tvTime)

        fun bind(item: NotificationResult, onClick: (NotificationResult) -> Unit) {
            text.text = item.notificationText
            time.text = item.createdAt // 필요하면 포맷팅
            itemView.setOnClickListener {
                onClick(item)
            }
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
