package com.example.planup.main.record.ui

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.planup.R
import com.example.planup.databinding.FragmentRecordWithFriendsBinding
import com.example.planup.main.record.adapter.PhotoAdapter
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter

class RecordWithFriendsFragment : Fragment() {
    lateinit var binding: FragmentRecordWithFriendsBinding
    private lateinit var photoAdapter: PhotoAdapter

    companion object {
        private const val ARG_GOAL_ID = "arg_goal_id"
        private const val ARG_GOAL_TITLE = "arg_goal_title"

        fun newInstance(goalId: Int?, goalTitle: String?): RecordWithFriendsFragment {
            return RecordWithFriendsFragment().apply {
                arguments = Bundle().apply {
                    goalId?.let { putInt(ARG_GOAL_ID, it) }
                    putString(ARG_GOAL_TITLE, goalTitle)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecordWithFriendsBinding.inflate(inflater, container, false)

        // 사진 그리드
        val sampleImages = listOf(
            R.drawable.img_sample1, R.drawable.img_sample2, R.drawable.img_sample3,
            R.drawable.img_sample4, R.drawable.img_sample5, R.drawable.img_sample6
        )
        binding.photoGridrv.apply {
            layoutManager = GridLayoutManager(requireContext(), 4)
            adapter = PhotoAdapter(sampleImages)
            val spacing = resources.getDimensionPixelSize(R.dimen.item_vertical_spacing)
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: android.graphics.Rect, view: View, parent: RecyclerView, state: RecyclerView.State
                ) {
                    val pos = parent.getChildAdapterPosition(view)
                    if (pos >= 0) {
                        outRect.top = spacing / 2
                        outRect.bottom = spacing / 2
                    }
                }
            })
        }

        // 단순 바차트(요일)
        val barChart = binding.barChart
        val entries = listOf(
            BarEntry(0f, 2f), BarEntry(1f, 6f), BarEntry(2f, 5f), BarEntry(3f, 7f),
            BarEntry(4f, 1f), BarEntry(5f, 4f), BarEntry(6f, 5f), BarEntry(7f, 5f)
        )
        barChart.apply {
            data = BarData(BarDataSet(entries, "요일별 기록").apply {
                color = Color.parseColor("#508CFF")
                valueTextColor = Color.TRANSPARENT
            }).apply { barWidth = 0.5f }
            description.isEnabled = false
            setDrawGridBackground(false)
            axisLeft.isEnabled = false
            axisRight.isEnabled = false
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                valueFormatter = IndexAxisValueFormatter(listOf("월","화","수","목","금","토","일","평균"))
                setDrawGridLines(false)
                granularity = 1f
                textColor = Color.parseColor("#4B4B4B")
                textSize = 12f
            }
            legend.isEnabled = false
            setScaleEnabled(false)
            setPinchZoom(false)
            invalidate()
        }

        // CombinedChart (라운드+그라데이션 막대 + 연노랑 라인)
        val labels = listOf("4월 4주차", "4월 5주차", "이번 주")
        val barValues = listOf(5f, 80f, 25f)
        val lineValues = listOf(5f, 80f, 25f)

        val barEntries = barValues.mapIndexed { i, v -> BarEntry(i.toFloat(), v) }

// ⬇️ 그라데이션 적용
        val barDataSet = BarDataSet(barEntries, "").apply {
            setGradientColor(
                Color.parseColor("#AFC6FF"), // start
                Color.parseColor("#3D63FF")  // end
            )
            valueTextColor = Color.parseColor("#3D63FF")
            valueTextSize = 14f
            valueFormatter = PercentFormatter()
            setDrawValues(true)
            highLightAlpha = 0
        }
        val barData2 = BarData(barDataSet).apply { barWidth = 0.6f }

// 라인 그대로
        val lineEntries = lineValues.mapIndexed { i, v -> Entry(i.toFloat(), v) }
        val lineDataSet = LineDataSet(lineEntries, "").apply {
            color = Color.parseColor("#FFE682")
            circleRadius = 6f
            setCircleColor(Color.parseColor("#FFE682"))
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

        binding.combinedChart.apply {
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

        // 뒤로가기 처리
        binding.btnBack.setOnClickListener { parentFragmentManager.popBackStack() }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() = parentFragmentManager.popBackStack()
            }
        )

        return binding.root
    }
}