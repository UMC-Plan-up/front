package com.example.planup.main.friend.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.planup.R
import com.example.planup.databinding.ItemFriendBinding
import com.example.planup.main.friend.data.FriendInfo

class FriendAdapter(
    private val onArrowClick: (FriendInfo) -> Unit
) : RecyclerView.Adapter<FriendAdapter.FriendViewHolder>() {

    private var itemList: List<FriendInfo> = emptyList()

    fun setItems(items: List<FriendInfo>) {
        itemList = items
        notifyDataSetChanged()
    }

    inner class FriendViewHolder(
        private val binding: ItemFriendBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun onBind(
            friend: FriendInfo
        ) {
            with(binding) {
                nicknameTv.text = friend.nickname
                goalCountTv.text = "${friend.goalCnt}개의 목표 진행 중"
                todayTimeTv.text = "오늘 진행 시간 ${friend.todayTime}"

                // ✅ 항상 노출
                photoVerifyBtn.visibility = View.VISIBLE

                arrowRightIc.isClickable = true
                arrowRightIc.setOnClickListener { onArrowClick(friend) }

                // ✅ 강조(선택): 새 인증 여부에 따라 스타일만 변경
                if (friend.isNewPhotoVerify) {
                    with(photoVerifyBtn) {
                        isEnabled = true
                        alpha = 1f
                        setBackgroundResource(R.drawable.rounded_box_lightblue)
                    }
                } else {
                    with(photoVerifyBtn) {
                        isEnabled = false
                        alpha = 0.6f
                        setBackgroundResource(R.drawable.rounded_box_lightblue)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val binding = ItemFriendBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FriendViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val friend = itemList[position]
        holder.onBind(friend)
    }

    override fun getItemCount(): Int = itemList.size
}