package com.example.planup.main.friend.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.planup.R
import com.example.planup.network.dto.friend.FriendRequest

class FriendRequestAdapter(
    private val items: List<FriendRequest>,
    private val onAcceptClick: (FriendRequest) -> Unit,
    private val onDeclineClick: (FriendRequest) -> Unit
) : RecyclerView.Adapter<FriendRequestAdapter.FriendRequestViewHolder>() {

    inner class FriendRequestViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNickname: TextView = view.findViewById(R.id.friend_nickname_tv)
        val tvStatus: TextView = view.findViewById(R.id.friend_goal_cnt_tv)
        val btnAccept: TextView = view.findViewById(R.id.btnAccept)
        val btnDecline: TextView = view.findViewById(R.id.btnDecline)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendRequestViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_friend_request, parent, false)
        return FriendRequestViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendRequestViewHolder, position: Int) {
        val item: FriendRequest = items[position]
        holder.tvNickname.text = item.nickname
        holder.tvStatus.text = item.status

        holder.btnAccept.setOnClickListener { onAcceptClick(item) }
        holder.btnDecline.setOnClickListener { onDeclineClick(item) }
    }

    override fun getItemCount(): Int = items.size
}