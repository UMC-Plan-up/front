package com.example.planup.main.home.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.planup.R
import com.example.planup.main.home.data.FriendTimer
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.planup.databinding.ItemFriendTimerBinding

class FriendTimerAdapter(private val friends: List<FriendTimer>) :
    RecyclerView.Adapter<FriendTimerAdapter.FriendViewHolder>() {

    inner class FriendViewHolder(binding: ItemFriendTimerBinding) : RecyclerView.ViewHolder(binding.root) {
        val profile = binding.friendTimerProfileIv
        val nickname = binding.friendTimerNicknameTv
        val timer = binding.friendTimerTv
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val binding = ItemFriendTimerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FriendViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val item = friends[position]
        holder.profile.setImageResource(item.profileResId)
        holder.nickname.text = item.nickname
        holder.timer.text = item.time
    }

    override fun getItemCount(): Int = friends.size
}