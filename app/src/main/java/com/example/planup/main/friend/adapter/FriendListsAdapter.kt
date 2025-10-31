package com.example.planup.main.friend.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.planup.R
import com.example.planup.network.dto.friend.FriendInfo

class FriendListsAdapter(
    private val items: List<FriendInfo>,
    private val onDeleteClick: (FriendInfo) -> Unit,
    private val onBlockClick: (FriendInfo) -> Unit,
    private val onReportClick: (FriendInfo) -> Unit
) : RecyclerView.Adapter<FriendListsAdapter.FriendListsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendListsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_friend_list, parent, false)
        return FriendListsViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendListsViewHolder, position: Int) {
        val item = items[position]
        holder.tvNickname.text = item.nickname

        // 버튼 콜백
        holder.btnDelete.setOnClickListener { onDeleteClick(item) }
        holder.btnBan.setOnClickListener { onBlockClick(item) }
        holder.btnReport.setOnClickListener { onReportClick(item) }
    }

    override fun getItemCount(): Int = items.size

    class FriendListsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNickname: TextView = view.findViewById(R.id.tv_nickname)
        val btnDelete: TextView = view.findViewById(R.id.btn_delete_friend1)
        val btnBan: TextView = view.findViewById(R.id.btn_ban_friend1)
        val btnReport: TextView = view.findViewById(R.id.btn_report_friend1)
    }
}