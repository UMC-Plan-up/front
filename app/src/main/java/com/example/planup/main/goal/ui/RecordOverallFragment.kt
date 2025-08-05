package com.example.planup.main.goal.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.planup.R
import com.example.planup.main.goal.item.GoalItem
import com.example.planup.main.goal.item.GoalAdapter
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class RecordOverallFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GoalAdapter
    private lateinit var dailyPieChart: PieChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_record_overall, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        dailyPieChart = view.findViewById(R.id.daily_goal_complete_pc)
        setupPieChart(dailyPieChart, 70)


        recyclerView = view.findViewById(R.id.goal_list_rv)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val goals = listOf(
            GoalItem(1, "목표명", "[기준 기간]&[빈도]&\"이상\"", 82),
            GoalItem(2, "토익 공부하기", "매주 5번 이상", 82),
            GoalItem(3, "헬스장 가기", "매일 30분 이상", 82)
        )

        adapter = GoalAdapter(goals) { goalItem ->
            val fragment = GoalDescriptionFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_container, fragment)
                .addToBackStack(null)
                .commit()
        }
        recyclerView.adapter = adapter
    }

    private fun setupPieChart(pieChart: PieChart, progress: Int) {
        val entries = listOf(
            PieEntry(progress.toFloat()),
            PieEntry((100 - progress).toFloat())
        )

        val dataSet = PieDataSet(entries, "").apply {
            colors = listOf(Color.WHITE, Color.rgb(220, 220, 220))
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
            holeRadius = 70f
            transparentCircleRadius = 0f
            centerText = ""
            invalidate()
        }
    }
}