package com.example.planup.main.home.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.planup.databinding.ItemDailyToDoBinding
import com.example.planup.main.home.data.DailyToDo
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import androidx.core.graphics.toColorInt

class DailyToDoAdapter :
    ListAdapter<DailyToDo, DailyToDoAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ItemDailyToDoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DailyToDo, position: Int) {
            binding.dailyTitleTv.text = item.title
            binding.dailyPercentageTv.text = "${item.progress}%"
            binding.dailyRateTv.text = "일일 달성률 ${item.dailyRate}%"

            setupPieChart(item.progress, position)
        }

        private fun setupPieChart(progress: Int, position: Int) {
            val entries = listOf(
                PieEntry(progress.toFloat()),
                PieEntry((100 - progress).toFloat())
            )

            val colors = when (position % 3) {
                0 -> listOf("#79B0F8".toColorInt(), Color.LTGRAY)
                1 -> listOf("#F3C092".toColorInt(), Color.LTGRAY)
                else -> listOf("#71D9C4".toColorInt(), Color.LTGRAY)
            }

            val dataSet = PieDataSet(entries, "").apply {
                setColors(colors)
                setDrawValues(false)
            }

            binding.dailyPieChartPc.apply {
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
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    class DiffCallback : DiffUtil.ItemCallback<DailyToDo>() {
        override fun areItemsTheSame(oldItem: DailyToDo, newItem: DailyToDo) =
            oldItem.title == newItem.title

        override fun areContentsTheSame(oldItem: DailyToDo, newItem: DailyToDo) =
            oldItem == newItem
    }
}
