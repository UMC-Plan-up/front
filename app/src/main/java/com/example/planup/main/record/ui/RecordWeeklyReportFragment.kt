package com.example.planup.main.record.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.planup.databinding.FragmentRecordWeeklyReportBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class RecordWeeklyReportFragment : Fragment() {
    lateinit var binding: FragmentRecordWeeklyReportBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecordWeeklyReportBinding.inflate(inflater, container, false)


        val barChart = binding.barChart

        val entries = listOf(
            BarEntry(0f, 2f),
            BarEntry(1f, 6f),
            BarEntry(2f, 5f),
            BarEntry(3f, 7f),
            BarEntry(4f, 1f),
            BarEntry(5f, 4f),
            BarEntry(6f, 5f),
            BarEntry(7f, 5f) // 평균
        )

        val dataSet = BarDataSet(entries, "요일별 기록")
        dataSet.color = Color.parseColor("#508CFF") // 파란색 계열
        dataSet.valueTextColor = Color.TRANSPARENT // 숫자 안 보이게

        val barData = BarData(dataSet)
        barData.barWidth = 0.5f

        barChart.data = barData
        barChart.description.isEnabled = false
        barChart.setDrawGridBackground(false)
        barChart.axisLeft.isEnabled = false
        barChart.axisRight.isEnabled = false
        barChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            valueFormatter = IndexAxisValueFormatter(
                listOf("월", "화", "수", "목", "금", "토", "일", "평균")
            )
            setDrawGridLines(false)
            granularity = 1f
            textColor = Color.parseColor("#4B4B4B")
            textSize = 12f
        }
        barChart.legend.isEnabled = false
        barChart.setScaleEnabled(false)
        barChart.setPinchZoom(false)
        barChart.invalidate() // 갱신
        return binding.root
    }

}