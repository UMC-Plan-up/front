package com.example.planup.main.home.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.planup.R
import com.example.planup.databinding.ItemFriendGoalListBinding
import com.example.planup.main.home.item.FriendGoalListItem
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class FriendGoalListAdapter(
    private val items: List<FriendGoalListItem>,
    private val onItemClick: (FriendGoalListItem) -> Unit   // 클릭 이벤트 콜백 추가
) : RecyclerView.Adapter<FriendGoalListAdapter.FriendGoalViewHolder>() {
    inner class FriendGoalViewHolder(val binding: ItemFriendGoalListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val pieChart = binding.friendGoalListPc
        val tvTitle = binding.friendGoalListTitleTv
        val tvNumber = binding.friendGoalListNumberTv
        val tvDescription = binding.friendGoalListDescriptionTv
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendGoalViewHolder {
        val binding = ItemFriendGoalListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FriendGoalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FriendGoalViewHolder, position: Int) {
        val item = items[position]

        holder.tvTitle.text = item.title
        holder.tvNumber.text = item.subtitle
        holder.tvDescription.text = item.description

        setupDonutChart(holder.pieChart, item.progress)

        // ✅ 클릭 이벤트 연결
        holder.binding.root.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = items.size

    private fun setupDonutChart(chart: PieChart, progress: Int) {
        val entries = listOf(
            PieEntry(progress.toFloat()),
            PieEntry((100 - progress).toFloat())
        )

        val dataSet = PieDataSet(entries, "").apply {
            setColors(Color.parseColor("#FFD700"), Color.parseColor("#EEEEEE"))
            valueTextColor = Color.TRANSPARENT
            sliceSpace = 2f
        }

        val pieData = PieData(dataSet)

        chart.apply {
            data = pieData
            description.isEnabled = false
            setUsePercentValues(false)
            setDrawEntryLabels(false)
            setHoleRadius(85f)
            setTransparentCircleRadius(75f)
            setDrawCenterText(true)
            centerText = "$progress%"
            setCenterTextSize(12f)
            legend.isEnabled = false
            setTouchEnabled(false)
            invalidate()
        }
    }
}

