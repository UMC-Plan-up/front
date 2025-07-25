package com.example.planup.main.record.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.planup.R
import com.example.planup.databinding.FragmentRecordWithFriendBinding
import com.example.planup.main.record.adapter.PhotoAdapter
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter

class RecordWithFriendFragment : Fragment() {
    lateinit var binding: FragmentRecordWithFriendBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var photoAdapter: PhotoAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecordWithFriendBinding.inflate(inflater, container, false)

        recyclerView = binding.photoRecyclerView

        val sampleImages = listOf(
            R.drawable.img_sample1, R.drawable.img_sample2, R.drawable.img_sample3,
            R.drawable.img_sample4, R.drawable.img_sample5, R.drawable.img_sample6
        )

        photoAdapter = PhotoAdapter(sampleImages)
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = photoAdapter

        val progressBar = binding.weekUploadPicturePb
        progressBar.progress = 75

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

        // CombinedChart 설정
        val combinedChart = binding.combinedChart
        val labels = listOf("4월 4주차", "4월 5주차", "이번 주")
        val barValues = listOf(5f, 80f, 25f)
        val lineValues = listOf(10f, 85f, 30f)

        val barEntries = barValues.mapIndexed { i, value -> BarEntry(i.toFloat(), value) }
        val barDataSet = BarDataSet(barEntries, "퍼센트").apply {
            color = Color.rgb(68, 109, 255)
            valueTextColor = Color.WHITE
            valueTextSize = 12f
            valueFormatter = PercentFormatter()
        }
        val barData2 = BarData(barDataSet)

        val lineEntries = lineValues.mapIndexed { i, value -> Entry(i.toFloat(), value) }
        val lineDataSet = LineDataSet(lineEntries, "추세선").apply {
            color = Color.YELLOW
            setDrawCircles(false)
            lineWidth = 2f
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawValues(false)
        }
        val lineData = LineData(lineDataSet)

        val combinedData = CombinedData().apply {
            setData(barData2)
            setData(lineData)
        }

        combinedChart.apply {
            description.isEnabled = false
            axisRight.isEnabled = false
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                valueFormatter = IndexAxisValueFormatter(labels)
                granularity = 1f
                textSize = 12f
                textColor = Color.BLACK
            }
            axisLeft.apply {
                axisMinimum = 0f
                textColor = Color.BLACK
                textSize = 12f
            }
            legend.isEnabled = false
            data = combinedData
            setScaleEnabled(false)
            setPinchZoom(false)
            invalidate()
        }

        return binding.root
    }

}