package com.example.planup.main.friend.adapter

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.planup.R
import com.example.planup.main.friend.data.FriendRequest

class FriendRequestAdapter(
    private val items: List<FriendRequest>
) : RecyclerView.Adapter<FriendRequestAdapter.FriendRequestViewHolder>() {

    inner class FriendRequestViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNickname: TextView = view.findViewById(R.id.tvNickname)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val btnAccept: TextView = view.findViewById(R.id.btnAccept)
        val btnDecline: TextView = view.findViewById(R.id.btnDecline)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendRequestViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_friend_request, parent, false)
        return FriendRequestViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendRequestViewHolder, position: Int) {
        val item = items[position]
        holder.tvNickname.text = item.nickname
        holder.tvStatus.text = item.status

        holder.btnAccept.setOnClickListener {
            showCustomToast(holder.itemView.context, "친구 신청을 수락했어요.")
        }

        holder.btnDecline.setOnClickListener {
            showCustomToast(holder.itemView.context, "친구 신청을 거절했어요.")
        }
    }

    override fun getItemCount(): Int = items.size

    private fun showCustomToast(context: Context, message: String) {
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.toast_friend_request, null)

        val toastText: TextView = layout.findViewById(R.id.toastText)
        toastText.text = message

        val toast = Toast(context)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        toast.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM, 0, 100)
        toast.show()
    }
}