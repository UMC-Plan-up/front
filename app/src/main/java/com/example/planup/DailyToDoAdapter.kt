package com.example.planup

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class DailyToDoAdapter(private val items: List<DailyToDo>) :
    RecyclerView.Adapter<DailyToDoAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle = itemView.findViewById<TextView>(R.id.daily_title_tv)
        private val tvPercentage = itemView.findViewById<TextView>(R.id.daily_percentage_tv)
        private val tvDailyRate = itemView.findViewById<TextView>(R.id.daily_rate_tv)
        private val pieChart = itemView.findViewById<PieChart>(R.id.daily_pieChart_pc)

        fun bind(item: DailyToDo) {
            tvTitle.text = item.title
            tvPercentage.text = "${item.progress}%"
            tvDailyRate.text = "일일 달성률 ${item.dailyRate}%"

            setupPieChart(item.progress)
        }

        private fun setupPieChart(progress: Int) {
            val entries = listOf(
                PieEntry(progress.toFloat()),
                PieEntry((100 - progress).toFloat())
            )

            val dataSet = PieDataSet(entries, "").apply {
                setColors(Color.rgb(33, 150, 243), Color.LTGRAY)
                setDrawValues(false)
            }

            pieChart.apply {
                data = PieData(dataSet)
                description.isEnabled = false
                legend.isEnabled = false
                setTouchEnabled(false)
                setHoleColor(Color.TRANSPARENT)
                setDrawHoleEnabled(true)
                setHoleRadius(70f)
                setDrawEntryLabels(false)
                invalidate()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_daily_to_do, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }
}