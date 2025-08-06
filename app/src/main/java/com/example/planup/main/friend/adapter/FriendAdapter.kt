package com.example.planup.main.friend.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.planup.R
import com.example.planup.main.friend.data.FriendInfo

class FriendAdapter(private val items: List<FriendInfo>) :
    RecyclerView.Adapter<FriendAdapter.FriendViewHolder>(){

    class FriendViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val tvNickname: TextView = view.findViewById(R.id.nickname_tv)
        val tvGoalCount: TextView = view.findViewById(R.id.goal_count_tv)
        val tvTodayTime: TextView = view.findViewById(R.id.today_time_tv)
        val btnPhotoVerify: TextView = view.findViewById(R.id.photo_verify_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder{
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_friend, parent, false)
        return FriendViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: FriendViewHolder,
        position: Int
    ) {
        val item = items[position]
        holder.tvNickname.text = item.nickname
        holder.tvGoalCount.text="${item.goalCnt}개의 목표 진행 중"
        holder.tvTodayTime.text = "오늘 진행 시간 ${item.todayTime}"
        holder.btnPhotoVerify.visibility = if (item.isNewPhotoVerify) View.VISIBLE else View.GONE
        Log.d("FriendAdapter", "Binding item: ${item.nickname}")
    }

    override fun getItemCount(): Int = items.size
}