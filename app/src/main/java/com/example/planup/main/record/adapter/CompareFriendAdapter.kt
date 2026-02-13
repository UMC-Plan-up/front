package com.example.planup.main.record.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.planup.R
import androidx.core.graphics.toColorInt

class CompareFriendAdapter :
    RecyclerView.Adapter<CompareFriendAdapter.VH>() {

    private val items = mutableListOf<CompareFriend>()

    fun submitList(list: List<CompareFriend>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val imgProfile: ImageView = view.findViewById(R.id.friend_compare_profile_iv)
        val tvName: TextView = view.findViewById(R.id.friend_compare_name_tv)
        val progressBar: ProgressBar = view.findViewById(R.id.friend_compare_pb)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_compare_friend, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]

        holder.imgProfile.setImageResource(item.imgUrl)
        holder.tvName.text = item.name
        holder.progressBar.progress = item.progress

        // Progress 색상 변경
        val drawable = holder.progressBar.progressDrawable.mutate()
        DrawableCompat.setTint(
            drawable,
            getColorByPosition(position)
        )
    }

    override fun getItemCount(): Int = items.size

    private fun getColorByPosition(position: Int): Int {
        return when (position % 4) {
            0 -> "#4A90E2".toColorInt() // 파랑
            1 -> "#F5C542".toColorInt() // 노랑
            2 -> "#F48FB1".toColorInt() // 핑크
            else -> "#4DB6AC".toColorInt() // 민트
        }
    }
}

data class CompareFriend(
    val imgUrl: Int,   // 또는 imageUrl
    val name: String,
    val progress: Int,          // 0~100
)

