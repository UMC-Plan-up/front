package com.example.planup

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.planup.databinding.FragmentRecordBinding
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import androidx.core.graphics.toColorInt

class RecordFragment : Fragment() {

    lateinit var binding: FragmentRecordBinding
    private lateinit var pieChart: PieChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_record, container, false)
        //pieChart = view.findViewById(R.id.pieChart)
        //setupPieChart()
        return view
    }
/*
    private fun setupPieChart() {
        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(70f, "달성"))
        entries.add(PieEntry(30f, "미달성"))

        val dataSet = PieDataSet(entries, "")
        dataSet.setColors(Color.parseColor("#FFFFFF"), Color.parseColor("#D9D9D9")) // 달성률, 회색 나머지
        dataSet.setDrawValues(false)

        val data = PieData(dataSet)
        val percent = 70
        pieChart.apply {
            this.data = data
            setDrawEntryLabels(false)
            description.isEnabled = false
            legend.isEnabled = false
            isRotationEnabled = false
            setDrawCenterText(true)
            //centerText = "${percent.toInt()}%"
            setCenterTextSize(14f)
            setHoleColor(Color.TRANSPARENT)
            holeRadius = 70f
            transparentCircleRadius = 0f
            invalidate()
        }
    }
*/
}