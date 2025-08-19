package com.example.planup.main.record.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.planup.R

class NotificationAdapter(
    private val onItemClick: (NotificationDTO) -> Unit
) : ListAdapter<NotificationDTO, NotificationAdapter.VH>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<NotificationDTO>() {
            override fun areItemsTheSame(old: NotificationDTO, new: NotificationDTO) =
                old.id == new.id

            override fun areContentsTheSame(old: NotificationDTO, new: NotificationDTO) =
                old == new
        }
    }

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivIcon: ImageView = itemView.findViewById(R.id.ivIcon)
        private val tvText: TextView = itemView.findViewById(R.id.tvText)

        fun bind(item: NotificationDTO) {
            // 필요 시 유형별 아이콘 변경 가능
            ivIcon.setImageResource(R.drawable.ic_deep_blue_circle)
            tvText.text = item.notificationText

            itemView.setOnClickListener { onItemClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }
}