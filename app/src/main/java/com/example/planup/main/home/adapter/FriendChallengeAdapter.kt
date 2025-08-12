package com.example.planup.main.home.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.planup.main.home.item.FriendChallengeItem
import android.widget.ImageView
import android.widget.TextView
import com.example.planup.R
import com.example.planup.databinding.ItemHomeFriendChallengeBinding
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class FriendChallengeAdapter(private val items: List<FriendChallengeItem>) :
    RecyclerView.Adapter<FriendChallengeAdapter.FriendChallengeViewHolder>() {

    inner class FriendChallengeViewHolder(binding: ItemHomeFriendChallengeBinding) : RecyclerView.ViewHolder(binding.root) {
        val img = binding.friendChallengeProfile
        val title = binding.friendChallengeTitleTv
        val desc = binding.friendChallengeDescriptionTv
        val pie1 = binding.friendChallengePc1
        val pie2 = binding.friendChallengePc2
        val pie3 = binding.friendChallengePc3
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendChallengeViewHolder {
        val binding = ItemHomeFriendChallengeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FriendChallengeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FriendChallengeViewHolder, position: Int) {
        val item = items[position]
        holder.img.setImageResource(item.profileResId)
        holder.title.text = item.name
        holder.desc.text = item.description

        listOf(holder.pie1, holder.pie2, holder.pie3).forEachIndexed { i, pie ->
            setupPieChart(pie, item.pieValues.getOrNull(i) ?: 0f)
        }
    }

    override fun getItemCount() = items.size

    private fun setupPieChart(pie: PieChart, value: Float) {
        pie.setExtraOffsets(-3f,-3f,-3f,-3f)
        pie.setUsePercentValues(false)
        pie.description.isEnabled = false
        pie.setDrawEntryLabels(false)
        pie.setDrawCenterText(true)
        pie.setTouchEnabled(false)
        pie.legend.isEnabled = false
        pie.holeRadius = 85f
        pie.setTransparentCircleAlpha(0)

        val entries = listOf(
            PieEntry(value),
            PieEntry(100 - value)
        )

        val colors = listOf(
            Color.BLUE, Color.LTGRAY
        )

        val dataSet = PieDataSet(entries, "").apply {
            setColors(colors)
            setDrawValues(false)
        }

        pie.data = PieData(dataSet)
        pie.centerText = "${value.toInt()}%"
        pie.invalidate()
    }
}