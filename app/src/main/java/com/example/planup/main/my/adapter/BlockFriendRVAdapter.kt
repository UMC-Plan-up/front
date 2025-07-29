package com.example.planup.main.my.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.planup.databinding.ItemMypageBlockFriendBinding
import com.example.planup.main.my.data.BlockFriend

class BlockFriendRVAdapter(private val friendList: ArrayList<BlockFriend>):RecyclerView.Adapter<BlockFriendRVAdapter.ViewHolder>() {

    interface FriendHandler{
        fun manageFriend(position: Int, action: Int)
    }

    private lateinit var friendHandler: FriendHandler

    fun setFriendHandler(friendHandler:FriendHandler){
        this.friendHandler = friendHandler
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMypageBlockFriendBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = friendList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(friendList[position])

        val unblock = 0
        val report = 1
        holder.binding.friendBlockUnblockIv.setOnClickListener {
            friendHandler.manageFriend(position, unblock)
        }
        holder.binding.friendBlockReportIv.setOnClickListener {
            friendHandler.manageFriend(position,report)
        }
    }

    inner class ViewHolder(val binding: ItemMypageBlockFriendBinding):RecyclerView.ViewHolder(binding.root){

        fun bind(blockFriend: BlockFriend){
            binding.friendBlockNameTv.setText(blockFriend.name)
            binding.friendBlockProfileIv.setImageResource(blockFriend.profile)
        }
    }
}