package com.example.planup.main.home.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.planup.main.goal.item.GoalItem
import com.example.planup.R
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class GoalAdapter(private val items: List<GoalItem>) :
    RecyclerView.Adapter<GoalAdapter.GoalViewHolder>() {

    class GoalViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val pieChart: PieChart = view.findViewById(R.id.goal_list_rv_pc)
        val title: TextView = view.findViewById(R.id.goal_list_title_tv)
        val description: TextView = view.findViewById(R.id.goal_list_description_tv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_goal_list_recyclerview, parent, false)
        return GoalViewHolder(view)
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.title
        holder.description.text = item.description
        setupDonutChart(holder.pieChart, item.progress)
    }

    override fun getItemCount(): Int = items.size

    private fun setupDonutChart(pieChart: PieChart, percent: Int) {
        val entries = listOf(
            PieEntry(percent.toFloat()),
            PieEntry((100 - percent).toFloat())
        )

        val dataSet = PieDataSet(entries, "").apply {
            colors = listOf(
                Color.rgb(51, 102, 255),
                Color.rgb(230, 230, 230)
            )
            setDrawValues(false)
            sliceSpace = 2f
        }

        pieChart.apply {
            data = PieData(dataSet)
            description.isEnabled = false
            legend.isEnabled = false
            setDrawEntryLabels(false)
            setTouchEnabled(false)
            isDrawHoleEnabled = true
            setHoleColor(Color.TRANSPARENT)
            holeRadius = 75f
            transparentCircleRadius = 0f
            centerText = "$percent%"
            setCenterTextSize(12f)
            setCenterTextColor(Color.BLACK)
            invalidate()
        }
    }
}