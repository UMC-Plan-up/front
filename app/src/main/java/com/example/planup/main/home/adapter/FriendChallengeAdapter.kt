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

class FriendChallengeAdapter(
    private val items: List<FriendChallengeItem>,
    private val onItemClick: (FriendChallengeItem) -> Unit // 콜백 추가
) : RecyclerView.Adapter<FriendChallengeAdapter.FriendChallengeViewHolder>() {

    inner class FriendChallengeViewHolder(
        private val binding: ItemHomeFriendChallengeBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        val img = binding.friendChallengeProfile
        val title = binding.friendChallengeTitleTv
        val desc = binding.friendChallengeDescriptionTv
        val pie1 = binding.friendChallengePc1
        val pie2 = binding.friendChallengePc2
        val pie3 = binding.friendChallengePc3

        fun bind(item: FriendChallengeItem) {
            img.setImageResource(item.profileResId)
            title.text = item.name
            desc.text = item.description

            val pieColors = listOf(
                Color.parseColor("#79B0F8"),
                Color.parseColor("#F3C092"),
                Color.parseColor("#71D9C4")
            )
            val remainderColor = Color.parseColor("#F2F2F2")

            listOf(pie1, pie2, pie3).forEachIndexed { i, pie ->
                setupPieChart(
                    pie,
                    item.pieValues.getOrNull(i) ?: 0f,
                    pieColors.getOrNull(i) ?: Color.BLUE,
                    remainderColor
                )
            }

            // 클릭 이벤트 처리
            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
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
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    private fun setupPieChart(pie: PieChart, value: Float, mainColor: Int, remainderColor: Int) {
        pie.setExtraOffsets(-3f, -3f, -3f, -3f)
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

        val colors = listOf(mainColor, remainderColor)

        val dataSet = PieDataSet(entries, "").apply {
            setColors(colors)
            setDrawValues(false)
        }

        pie.data = PieData(dataSet)
        pie.centerText = "${value.toInt()}%"
        pie.invalidate()
    }
}
