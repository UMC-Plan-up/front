package com.planup.planup.main.home.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.planup.planup.R
import com.planup.planup.main.goal.item.GetCommentsResult

class CommentAdapter(
    private val onMoreClick: (View, GetCommentsResult) -> Unit
) :
    ListAdapter<GetCommentsResult, CommentAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val nickname = view.findViewById<TextView>(R.id.comment_other_nickname_tv)
        private val content = view.findViewById<TextView>(R.id.comment_other_content_tv)
        private val profileImg = view.findViewById<ImageView>(R.id.comment_other_profile_iv)
        private val btnMore = view.findViewById<ImageView>(R.id.comment_other_more_iv)

        fun bind(item: GetCommentsResult) {
            nickname.text = item.writerNickname
            content.text = item.content
            Glide.with(itemView.context)
                .load(item.writerProfileImg)
                .placeholder(R.drawable.img_friend_profile_sample1)
                .error(R.drawable.img_friend_profile_sample1)
                .circleCrop()
                .into(profileImg)

            btnMore.setOnClickListener {
                onMoreClick(it, item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment_other, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<GetCommentsResult>() {
            override fun areItemsTheSame(oldItem: GetCommentsResult, newItem: GetCommentsResult) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: GetCommentsResult, newItem: GetCommentsResult) =
                oldItem == newItem
        }
    }
}