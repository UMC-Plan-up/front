package com.example.planup.main.friend.adapter

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.planup.R
import com.example.planup.main.friend.data.FriendLists

class FriendListsAdapter(
    private val items: List<FriendLists>
) : RecyclerView.Adapter<FriendListsAdapter.FriendListsViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FriendListsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_friend_list, parent, false)
        return FriendListsViewHolder(view, parent.context)
    }

    override fun onBindViewHolder(
        holder: FriendListsViewHolder,
        position: Int
    ) {
        val item = items[position]
        holder.tvNickname.text = item.nickname
    }

    override fun getItemCount(): Int = items.size

    inner class FriendListsViewHolder(view: View, private val context: Context) : RecyclerView.ViewHolder(view) {
        val tvNickname: TextView = view.findViewById(R.id.tv_nickname)

        private val btnDelete: TextView = view.findViewById(R.id.btn_delete_friend1)
        private val btnBan: TextView = view.findViewById(R.id.btn_ban_friend1)
        private val btnReport: TextView = view.findViewById(R.id.btn_report_friend1)

        init {
            btnDelete.setOnClickListener {
                showDialog(R.layout.dialog_delete_friend)
            }
            btnBan.setOnClickListener {
                showDialog(R.layout.dialog_ban_friend)
            }
            btnReport.setOnClickListener {
                showDialog(R.layout.dialog_report_friend)
            }
        }

        private fun showDialog(layoutId: Int) {
            val dialog = Dialog(context)
            dialog.setContentView(layoutId)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.window?.setDimAmount(0.5f)

            val noBtn = dialog.findViewById<ImageView>(R.id.popup_block_no_iv)
            val yesBtn = dialog.findViewById<ImageView>(R.id.popup_block_yes_iv)

            noBtn?.setOnClickListener {
                dialog.dismiss()
            }

            yesBtn?.setOnClickListener {
                // TODO: 각 상황에 따른 처리 추가
                dialog.dismiss()
            }

            dialog.show()
        }
    }
}