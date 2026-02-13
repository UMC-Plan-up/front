package com.planup.planup.main.record.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.planup.planup.R
import com.planup.planup.databinding.ItemNotificationBinding
import com.planup.planup.main.record.data.NotificationDTO

class NotificationAdapter(
    private val onItemClick: (NotificationDTO) -> Unit
) :
    ListAdapter<NotificationDTO, NotificationAdapter.NotificationViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemNotificationBinding.inflate(inflater, parent, false)
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    // ⭐️ 최대 3개만 표시
    override fun getItemCount(): Int {
        return minOf(currentList.size, 3)
    }

    inner class NotificationViewHolder(
        private val binding: ItemNotificationBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: NotificationDTO) {
            binding.tvText.text = item.notificationText
            binding.ivIcon.setImageResource(R.drawable.ic_deep_blue_circle)
            binding.tvText.text = item.notificationText

            itemView.setOnClickListener { onItemClick(item) }
        }
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<NotificationDTO>() {

            override fun areItemsTheSame(
                oldItem: NotificationDTO,
                newItem: NotificationDTO
            ): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: NotificationDTO,
                newItem: NotificationDTO
            ): Boolean = oldItem == newItem
        }
    }
}


