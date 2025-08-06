package com.example.planup.main.home.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.planup.R
import com.example.planup.main.home.item.FriendGoalListItem
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class FriendGoalListAdapter(private val items: List<FriendGoalListItem>) :
    RecyclerView.Adapter<FriendGoalListAdapter.FriendGoalViewHolder>() {

    inner class FriendGoalViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val pieChart: PieChart = view.findViewById(R.id.friend_goal_list_pc)
        val tvTitle: TextView = view.findViewById(R.id.friend_goal_list_title_tv)
        val tvNumber: TextView = view.findViewById(R.id.friend_goal_list_number_tv)
        val tvDescription: TextView = view.findViewById(R.id.friend_goal_list_description_tv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendGoalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_friend_goal_list, parent, false)
        return FriendGoalViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendGoalViewHolder, position: Int) {
        val item = items[position]

        holder.tvTitle.text = item.title
        holder.tvNumber.text = item.subtitle
        holder.tvDescription.text = item.description

        setupDonutChart(holder.pieChart, item.progress)
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
