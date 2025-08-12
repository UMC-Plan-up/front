package com.example.planup.goal.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.planup.R
import com.example.planup.databinding.ItemChallengeFriendBinding
import com.example.planup.network.data.ChallengeFriends

class FriendRVAdapter(val friends:List<ChallengeFriends>): RecyclerView.Adapter<FriendRVAdapter.ViewHolder>(){

    interface FriendListener{
        fun selectFriend(position: Int)
    }
    lateinit var friendListener:FriendListener
    fun setMyFriendListener(friendListener: FriendListener){
        this.friendListener = friendListener
    }
    private var lastSelected: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemChallengeFriendBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = friends.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(friends[position])
        holder.binding.root.isSelected = (position == lastSelected)

        holder.binding.root.setOnClickListener {
            if (lastSelected < 0){
                lastSelected = holder.bindingAdapterPosition
                notifyItemChanged(holder.bindingAdapterPosition)
            } else if(lastSelected != position){
                notifyItemChanged(lastSelected)
                lastSelected = holder.bindingAdapterPosition
                notifyItemChanged(holder.bindingAdapterPosition)
            }
            friendListener.selectFriend(position)
        }
    }

    inner class ViewHolder(val binding:ItemChallengeFriendBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(friend: ChallengeFriends){
            binding.challengeFriendProfileIv.setImageResource(R.drawable.profile_example)
            binding.challengeFriendNameTv.text = friend.nickname
            binding.challengeFriendGoalTv.text = binding.root.context.getString(R.string.challenge_friend_goal, friend.goalCnt)
        }
    }
}