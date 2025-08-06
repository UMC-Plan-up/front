package com.example.planup.main.home.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.planup.R
import com.example.planup.main.home.data.FriendTimer
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

class FriendTimerAdapter(private val friends: List<FriendTimer>) :
    RecyclerView.Adapter<FriendTimerAdapter.FriendViewHolder>() {

    inner class FriendViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val profile = view.findViewById<ImageView>(R.id.friend_timer_profile_iv)
        val nickname = view.findViewById<TextView>(R.id.friend_timer_nickname_tv)
        val timer = view.findViewById<TextView>(R.id.friend_timer_tv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_friend_timer, parent, false)
        return FriendViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val item = friends[position]
        holder.profile.setImageResource(item.profileResId)
        holder.nickname.text = item.nickname
        holder.timer.text = item.time
    }

    override fun getItemCount(): Int = friends.size
}