package com.planup.planup.goal.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.planup.planup.R
import com.planup.planup.goal.util.loadProfile

data class CommentItem(val profile: String?,val nickname: String, val comment: String)

class CommentAdapter(private val comments: List<CommentItem>)
    : RecyclerView.Adapter<CommentViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment_other, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        comments[position].run {
            holder.profileImage.loadProfile(profile)
            holder.nicknameText.text = nickname
            holder.commentText.text = comment
        }
    }

    override fun getItemCount(): Int = comments.size
}

class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val profileImage: ImageView = itemView.findViewById(R.id.comment_other_profile_iv)
    val nicknameText: TextView = itemView.findViewById(R.id.comment_other_nickname_tv)
    val commentText: TextView = itemView.findViewById(R.id.textSpeech)
}