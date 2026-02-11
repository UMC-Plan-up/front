package com.planup.planup.main.record.ui

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.planup.planup.R
import com.planup.planup.databinding.FragmentRecordWithCommunityBinding
import com.planup.planup.main.home.item.CustomCombinedChartRenderer
import com.planup.planup.main.record.adapter.PhotoAdapter
import com.planup.planup.main.record.adapter.RankAdapter
import com.planup.planup.main.record.adapter.RankItem
import com.planup.planup.main.record.data.DailyAchievementRateDto
import com.planup.planup.main.record.data.ThreeWeekAchievementRateDto
import com.planup.planup.main.record.ui.viewmodel.RecordGoalReportViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
@AndroidEntryPoint
class RecordWithCommunityFragment @Inject constructor(): Fragment() {
    lateinit var binding: FragmentRecordWithCommunityBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var photoAdapter: PhotoAdapter
    private val viewModel: RecordGoalReportViewModel by viewModels()

    companion object {
        private const val ARG_GOAL_ID = "arg_goal_id"
        private const val ARG_GOAL_TITLE = "arg_goal_title"

        fun newInstance(goalId: Int?, goalTitle: String?): RecordWithCommunityFragment {
            return RecordWithCommunityFragment().apply {
                arguments = Bundle().apply {
                    goalId?.let { putInt(ARG_GOAL_ID, it) }
                    putString(ARG_GOAL_TITLE, goalTitle)
                }
            }
        }
    }

    fun toBarEntry(item: DailyAchievementRateDto): List<BarEntry> {
        val entries = listOf(
            BarEntry(0f, item.sun.toFloat()),
            BarEntry(1f, item.mon.toFloat()),
            BarEntry(2f, item.tue.toFloat()),
            BarEntry(3f, item.wed.toFloat()),
            BarEntry(4f, item.thu.toFloat()),
            BarEntry(5f, item.fri.toFloat()),
            BarEntry(6f, item.sat.toFloat())
        )
        return entries
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecordWithCommunityBinding.inflate(inflater, container, false)
        viewModel.loadWeeklyGoalReport()
        viewModel.loadPhotos()
        // ====== 사진 그리드 ======
        val sampleImages = listOf(
            R.drawable.img_sample1, R.drawable.img_sample2, R.drawable.img_sample3,
            R.drawable.img_sample4, R.drawable.img_sample5, R.drawable.img_sample6
        )
        val images = viewModel.photos.value.map { it.photoImg }
        binding.photoGridrv.apply {
            layoutManager = GridLayoutManager(requireContext(), 4)
            adapter = PhotoAdapter(images)
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

        // ====== 요일별 바차트(단순) ======
        val barChart = binding.barChart
        val sampleEntries = listOf(
            BarEntry(0f, 2f), BarEntry(1f, 6f), BarEntry(2f, 5f), BarEntry(3f, 7f),
            BarEntry(4f, 1f), BarEntry(5f, 4f), BarEntry(6f, 5f), BarEntry(7f, 5f)
        )
        val dailyAchievementRate = viewModel.dailyAchievementRate.value
        val entries = toBarEntry(dailyAchievementRate)
        val dataSet = BarDataSet(entries, "요일별 기록").apply {
            color = Color.parseColor("#508CFF")
            valueTextColor = Color.TRANSPARENT
        }
        barChart.apply {
            data = BarData(dataSet).apply { barWidth = 0.5f }
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

        // ====== CombinedChart (라운드+그라데이션 막대 + 연노랑 라인) ======
        // CombinedChart 설정
        val sampleCombinedEntries = ThreeWeekAchievementRateDto(
            10, 80, 25
        )
        val combinedEntries = viewModel.threeWeekAchievementRate.value
        setupCombinedChart(combinedEntries)

        // ====== 랭킹 리스트 ======
        val sampleRankData = listOf(
            RankItem(4, "닉네임4", 12, R.drawable.img_friend_profile_sample4),
            RankItem(5, "닉네임5", 10, R.drawable.img_friend_profile_sample2),
            RankItem(6, "닉네임6", 8, R.drawable.img_friend_profile_sample3),
            RankItem(7, "닉네임7", 6, R.drawable.img_friend_profile_sample4),
        )
        val rankData = viewModel.reportUsers.value
        // TODO:: 한준님이 원복
//        val rankAdapter = RankAdapter(rankData)
//        binding.rankRecyclerView.apply {
//            layoutManager = LinearLayoutManager(requireContext())
//            adapter = rankAdapter
//        }

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

    private fun setupCombinedChart(temp: ThreeWeekAchievementRateDto) {
        val labels = listOf("4월 4주차", "4월 5주차", "이번 주")
        val entries = listOf(
            BarEntry(0f, temp.twoWeekBefore.toFloat()),
            BarEntry(1f, temp.oneWeekBefore.toFloat()),
            BarEntry(2f, temp.thisWeek.toFloat())
        )
        val barEntries = entries

        val lineEntries = entries

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
        val chart = binding.combinedChart
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
}