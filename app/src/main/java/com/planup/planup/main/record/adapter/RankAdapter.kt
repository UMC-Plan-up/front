package com.planup.planup.main.record.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import coil3.Uri
import coil3.toUri
import com.planup.planup.R

data class RankItem(val rank: Int, val nickname: String, val certCount: Int, val imageResId: String)

class RankAdapter(private val rankList: List<RankItem>) :
    RecyclerView.Adapter<RankAdapter.RankViewHolder>() {

    class RankViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rankText: TextView = itemView.findViewById(R.id.rankText)
        val profileImage: ImageView = itemView.findViewById(R.id.profileImage)
        val nicknameText: TextView = itemView.findViewById(R.id.nicknameText)
        val certCountText: TextView = itemView.findViewById(R.id.certCountText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rank, parent, false)
        return RankViewHolder(view)
    }

    override fun onBindViewHolder(holder: RankViewHolder, position: Int) {
        val item = rankList[position]
        holder.rankText.text = "${item.rank}위"
        holder.nicknameText.text = item.nickname
        holder.certCountText.text = "사진인증 ${item.certCount}회"
        holder.profileImage.setImageURI(item.imageResId.toUri())
    }

    override fun getItemCount(): Int = rankList.size
}