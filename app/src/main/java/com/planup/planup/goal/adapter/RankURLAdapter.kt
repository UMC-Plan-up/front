package com.planup.planup.goal.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.crossfade
import com.planup.planup.R

data class RankURLItem(val rank: Int, val nickname: String, val certCount: Int, val imageResId: String)

class RankURLAdapter(private val rankList: List<RankURLItem>) :
    RecyclerView.Adapter<RankURLAdapter.RankURLViewHolder>() {

    class RankURLViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rankText: TextView = itemView.findViewById(R.id.rankText)
        val profileImage: ImageView = itemView.findViewById(R.id.profileImage)
        val nicknameText: TextView = itemView.findViewById(R.id.nicknameText)
        val certCountText: TextView = itemView.findViewById(R.id.certCountText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankURLViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rank, parent, false)
        return RankURLViewHolder(view)
    }

    override fun onBindViewHolder(holder: RankURLViewHolder, position: Int) {
        val item = rankList[position]
        holder.rankText.text = "${item.rank}위"
        holder.nicknameText.text = item.nickname
        holder.certCountText.text = "사진인증 ${item.certCount}회"
        holder.profileImage.load(item.imageResId) {
            crossfade(true)
            error(R.drawable.ic_profile)
        }
    }

    override fun getItemCount(): Int = rankList.size

    fun Context.isValidImageUri(uri: Uri): Boolean {
        return try {
            contentResolver.openInputStream(uri)?.use { true } ?: false
        } catch (e: Exception) {
            false
        }
    }
}