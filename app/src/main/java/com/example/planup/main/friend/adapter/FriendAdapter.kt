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

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val item = items[position]
        holder.tvNickname.text = item.nickname
        holder.tvGoalCount.text = "${item.goalCnt}개의 목표 진행 중"
        holder.tvTodayTime.text = "오늘 진행 시간 ${item.todayTime}"

        // ✅ 항상 노출
        holder.btnPhotoVerify.visibility = View.VISIBLE

        // ✅ 강조(선택): 새 인증 여부에 따라 스타일만 변경
        if (item.isNewPhotoVerify) {
            holder.btnPhotoVerify.isEnabled = true
            holder.btnPhotoVerify.alpha = 1f
            holder.btnPhotoVerify.setBackgroundResource(R.drawable.rounded_box_lightblue)
        } else {
            holder.btnPhotoVerify.isEnabled = false       // 탭 비활성화만 원하면 유지
            holder.btnPhotoVerify.alpha = 0.6f            // 흐리게
            holder.btnPhotoVerify.setBackgroundResource(R.drawable.rounded_box_lightblue) // 필요시 다른 회색 배경으로 교체
        }
    }

    override fun getItemCount(): Int = items.size
}