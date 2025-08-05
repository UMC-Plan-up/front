package com.example.planup.main.record.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.planup.R
import com.example.planup.databinding.FragmentRecordWithFriendsBinding
import com.example.planup.main.record.adapter.PhotoAdapter
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter

class RecordWithFriendsFragment : Fragment() {
    lateinit var binding: FragmentRecordWithFriendsBinding
    private lateinit var photoAdapter: PhotoAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecordWithFriendsBinding.inflate(inflater, container, false)


        val sampleImages = listOf(
            R.drawable.img_sample1, R.drawable.img_sample2, R.drawable.img_sample3,
            R.drawable.img_sample4, R.drawable.img_sample5, R.drawable.img_sample6
        )

        val gridRecyclerView = binding.photoGridrv
        val gridAdapter = PhotoAdapter(sampleImages)
        gridRecyclerView.layoutManager = GridLayoutManager(requireContext(), 4)
        gridRecyclerView.adapter = gridAdapter
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.item_vertical_spacing)
        binding.photoGridrv.addItemDecoration(object : RecyclerView.ItemDecoration(){
            override fun getItemOffsets(
                outRect: android.graphics.Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ){
                val position = parent.getChildAdapterPosition(view)
                if(position >= 0){
                    outRect.top = spacingInPixels / 2
                    outRect.bottom = spacingInPixels / 2
                }
            }
        })


        val barChart = binding.barChart

        val entries = listOf(
            BarEntry(0f, 2f),
            BarEntry(1f, 6f),
            BarEntry(2f, 5f),
            BarEntry(3f, 7f),
            BarEntry(4f, 1f),
            BarEntry(5f, 4f),
            BarEntry(6f, 5f),
            BarEntry(7f, 5f)
        )

        val dataSet = BarDataSet(entries, "요일별 기록")
        dataSet.color = Color.parseColor("#508CFF")
        dataSet.valueTextColor = Color.TRANSPARENT

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
        barChart.invalidate()

        val combinedChart = binding.combinedChart
        val labels = listOf("4월 4주차", "4월 5주차", "이번 주")
        val barValues = listOf(5f, 80f, 25f)
        val lineValues = listOf(5f, 80f, 25f)

        val barEntries = barValues.mapIndexed { i, value -> BarEntry(i.toFloat(), value) }
        val barDataSet = BarDataSet(barEntries, "").apply {
            color = Color.parseColor("#6799FF")
            valueTextColor = Color.parseColor("#6799FF")
            valueTextSize = 14f
            valueFormatter = PercentFormatter()
            setDrawValues(true)
            highLightAlpha = 0
        }
        val barData2 = BarData(barDataSet).apply {
            barWidth = 0.6f
        }

        val lineEntries = lineValues.mapIndexed { i, value -> Entry(i.toFloat(), value) }
        val lineDataSet = LineDataSet(lineEntries, "").apply {
            color = Color.YELLOW
            circleRadius = 6f
            setCircleColor(Color.YELLOW)
            setDrawCircleHole(false)
            lineWidth = 2f
            valueTextSize = 0f
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

            axisLeft.apply {
                axisMinimum = 0f
                axisMaximum = 100f
                textColor = Color.BLACK
                textSize = 12f
                granularity = 10f
            }

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                valueFormatter = IndexAxisValueFormatter(labels)
                granularity = 1f
                textSize = 14f
                textColor = Color.BLACK
                setDrawGridLines(false)
                axisMinimum = -0.5f
                axisMaximum = labels.size - 0.5f
            }

            legend.isEnabled = false
            extraTopOffset = 20f
            extraBottomOffset = 50f
            data = combinedData
            setScaleEnabled(false)
            setPinchZoom(false)
            invalidate()
        }

        return binding.root
    }
}