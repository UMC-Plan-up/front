package com.example.planup.main.home.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import com.example.planup.R
import com.example.planup.databinding.ItemDailyToDoBinding
import com.example.planup.main.home.data.DailyToDo
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class DailyToDoAdapter(private val items: List<DailyToDo>) :
    RecyclerView.Adapter<DailyToDoAdapter.ViewHolder>() {
    inner class ViewHolder(binding: ItemDailyToDoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val tvTitle = binding.dailyTitleTv
        private val tvPercentage = binding.dailyPercentageTv
        private val tvDailyRate = binding.dailyRateTv
        private val pieChart = binding.dailyPieChartPc

        fun bind(item: DailyToDo, position: Int) {
            tvTitle.text = item.title
            tvPercentage.text = "${item.progress}%"
            tvDailyRate.text = "일일 달성률 ${item.dailyRate}%"

            setupPieChart(item.progress, position)
        }

        private fun setupPieChart(progress: Int, position: Int) {
            val entries = listOf(
                PieEntry(progress.toFloat()),
                PieEntry((100 - progress).toFloat())
            )

            // 아이템마다 다른 색 지정
            val colors = when (position % 3) {  // 3가지 색 반복
                0 -> listOf(Color.parseColor("#79B0F8"), Color.LTGRAY)
                1 -> listOf(Color.parseColor("#F3C092"), Color.LTGRAY)
                else -> listOf(Color.parseColor("#71D9C4"), Color.LTGRAY)
            }

            val dataSet = PieDataSet(entries, "").apply {
                setColors(colors)
                setDrawValues(false)
            }

            pieChart.apply {
                data = PieData(dataSet)
                description.isEnabled = false
                legend.isEnabled = false
                setTouchEnabled(false)
                setHoleColor(Color.TRANSPARENT)
                setDrawHoleEnabled(true)
                setHoleRadius(80f)
                setDrawEntryLabels(false)
                invalidate()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDailyToDoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_daily_to_do, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], position)
    }
}