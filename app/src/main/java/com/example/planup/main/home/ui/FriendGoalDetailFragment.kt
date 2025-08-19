package com.example.planup.main.home.ui


import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.planup.databinding.FragmentFriendGoalDetailBinding
import com.example.planup.main.home.item.CustomCombinedChartRenderer
import com.example.planup.main.home.item.RoundedBarChartRenderer
import com.github.mikephil.charting.charts.CombinedChart
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
import com.github.mikephil.charting.utils.ColorTemplate
import androidx.core.graphics.toColorInt

class FriendGoalDetailFragment : Fragment() {

    private var _binding: FragmentFriendGoalDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var chart: CombinedChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFriendGoalDetailBinding.inflate(inflater, container, false)
        chart = binding.friendGoalChart
        setupCombinedChart()

        binding.friendGoalCheerBtn.setOnClickListener {
            // TODO: 응원하기 클릭 이벤트
        }

        binding.friendGoalMotivateBtn.setOnClickListener {
            // TODO: 칭찬하기 클릭 이벤트
        }

        binding.friendGoalDetailBackIv.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

//        binding.btnSend.setOnClickListener {
//            val comment = binding.etComment.text.toString()
//            if (comment.isNotEmpty()) {
//                // TODO: 댓글 저장 처리
//                binding.etComment.text.clear()
//            }
//        }

        return binding.root
    }

    private fun setupCombinedChart() {
        val labels = listOf("4월 4주차", "4월 5주차", "이번 주")

        val barEntries = listOf(
            BarEntry(0f, 5f),
            BarEntry(1f, 80f),
            BarEntry(2f, 25f)
        )

        val lineEntries = listOf(
            Entry(0f, 5f),
            Entry(1f, 80f),
            Entry(2f, 25f)
        )

        // Bar 데이터셋
        val barDataSet = BarDataSet(barEntries, "달성률").apply {
            color = "#548DFF".toColorInt()
            valueTextSize = 14f
            valueTextColor = "#5C91FC".toColorInt()
            valueFormatter = PercentFormatter()
        }

        // Line 데이터셋
        val lineDataSet = LineDataSet(lineEntries, "추이").apply {
            color = Color.YELLOW
            circleRadius = 5f
            setCircleColor(Color.YELLOW)
            lineWidth = 2f
            mode = LineDataSet.Mode.LINEAR
            setDrawValues(false)
        }

        val barData = BarData(barDataSet).apply {
            barWidth = 0.4f
        }
        val lineData = LineData(lineDataSet)

        val combinedData = CombinedData().apply {
            setData(barData)
            setData(lineData)
        }

        chart.apply {
            data = combinedData
            description.isEnabled = false
            legend.isEnabled = false

            // X축
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                valueFormatter = IndexAxisValueFormatter(labels)
                granularity = 1f
                setDrawGridLines(false)
                axisLineColor = Color.GRAY

                axisMinimum = -0.5f // ← 첫 번째 바 왼쪽 여유
                axisMaximum = labels.size - 0.5f // ← 마지막 바 오른쪽 여유
            }

            // Y축
            axisLeft.apply {
                axisMinimum = 0f
                axisMaximum = 100f
                granularity = 25f            // 눈금 단위
                setDrawGridLines(false)       // 그리드 라인 제거
                setDrawAxisLine(true)         // 축선 표시
                axisLineColor = Color.GRAY    // 축선 색상
                axisLineWidth = 1f            // 축선 두께
            }
            axisRight.isEnabled = false      // 오른쪽 Y축 사용 안함

            renderer = CustomCombinedChartRenderer(chart, chart.animator, chart.viewPortHandler)
            invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
