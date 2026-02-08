package com.example.planup.main.record.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.planup.R
import com.example.planup.databinding.ItemBadgeBinding
import com.example.planup.databinding.ItemBadgeHeaderBinding

sealed class BadgeRow {
    data class Header(val title: String) : BadgeRow()

    data class Item(
        val badgeType: String,   // 서버 enum 값
        val title: String,
        val lockedImageRes: Int,
        val unlockedImageRes: Int,
        val isUnlocked: Boolean = false
    ) : BadgeRow()
}

class BadgeSectionAdapter(
    items: List<BadgeRow> = emptyList()
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<BadgeRow>().apply {
        addAll(items)
    }

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
    }

    override fun getItemViewType(position: Int): Int =
        when (items[position]) {
            is BadgeRow.Header -> TYPE_HEADER
            is BadgeRow.Item -> TYPE_ITEM
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inf = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_HEADER) {
            val b = ItemBadgeHeaderBinding.inflate(inf, parent, false)
            HeaderVH(b)
        } else {
            val b = ItemBadgeBinding.inflate(inf, parent, false)
            ItemVH(b)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val row = items[position]) {
            is BadgeRow.Header -> (holder as HeaderVH).bind(row)
            is BadgeRow.Item -> (holder as ItemVH).bind(row)
        }
    }

    override fun getItemCount(): Int = items.size

    fun submitList(newItems: List<BadgeRow>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    class HeaderVH(private val b: ItemBadgeHeaderBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(h: BadgeRow.Header) { b.tvHeader.text = h.title }
    }

    class ItemVH(private val b: ItemBadgeBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(i: BadgeRow.Item) {
            b.imgBadge.setImageResource(
                if (i.isUnlocked) i.unlockedImageRes
                else i.lockedImageRes
            )
            b.tvBadge.text = i.title
        }
    }
}