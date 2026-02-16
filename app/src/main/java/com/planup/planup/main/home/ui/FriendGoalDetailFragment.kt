package com.planup.planup.main.home.ui


import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.planup.planup.databinding.FragmentFriendGoalDetailBinding
import com.planup.planup.main.home.item.CustomCombinedChartRenderer
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
import androidx.core.graphics.toColorInt
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import retrofit2.HttpException
import com.planup.planup.R
import com.planup.planup.main.goal.item.CreateCommentRequest
import com.planup.planup.main.home.adapter.PhotoAdapter
import com.planup.planup.main.home.ui.viewmodel.FriendGoalDetailViewModel
import com.planup.planup.network.ApiResult
import com.planup.planup.network.RetrofitInstance
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FriendGoalDetailFragment : Fragment() {

    private var _binding: FragmentFriendGoalDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var chart: CombinedChart
    private val viewModel: FriendGoalDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFriendGoalDetailBinding.inflate(inflater, container, false)
        val title = viewModel.title
        binding.friendDetailTitleTv.text = title
        binding.friendDetailWeekFocusTv.text = getString(R.string.friend_detail_week_focus, title)
        binding.friendGoalDetailTodayfocusTv.text = getString(R.string.friend_goal_detail_today_text, title)
        loadComment()
        loadTodayFriendTime()
        loadFriendPhotos()

        chart = binding.friendGoalChart
        setupCombinedChart()

        binding.friendGoalCheerBtn.setOnClickListener {
            viewModel.sendReaction(false)
        }

        binding.friendGoalMotivateBtn.setOnClickListener {
            viewModel.sendReaction(true)
        }

        binding.friendGoalDetailBackIv.setOnClickListener {
            parentFragmentManager.popBackStack()
        }



        binding.friendGoalSendCommentIv.setOnClickListener {
            val comment = binding.friendGoalCommentEt.text.toString()
            if (comment.isNotEmpty()) {
                sendComment(comment)
            }
        }

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

    private fun loadComment() {
        viewModel.loadComment(createErrorHandler("loadComment") {
            binding.friendGoalCommentTv.text = it[0].content
            binding.friendGoalOtherNicknameTv.text = it[0].writerNickname
            Glide.with(this@FriendGoalDetailFragment)
                .load(it[0].writerProfileImg)
                .error(R.drawable.img_friend_profile_sample1)
                .into(binding.friendGoalOtherProfileIv)
        })
    }

    private fun loadTodayFriendTime(){
        viewModel.loadTodayFriendTime(createErrorHandler("loadTodayFriendTime") {
            binding.goalDetailTodayTimerTv.text = it
        })
    }

    private fun loadFriendPhotos() {
        viewModel.loadFriendPhoto(createErrorHandler("loadFriendPhotos") { result ->
            val urls = result.map { it.photoImg }
            setupRecyclerView(urls)
        })
    }
    private fun setupRecyclerView(photoUrls: List<String>) {
        val recyclerView = binding.photoRecyclerView
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 4) // 4열
        recyclerView.adapter = PhotoAdapter(photoUrls)
    }

    private fun sendComment(comment: String) {
        viewModel.sendComment(comment,createErrorHandler("sendComment") {
            binding.friendGoalCommentEt.text.clear()
            loadComment()
        })
    }

    fun <T> createErrorHandler(
        tag: String,
        onSuccess: ((T) -> Unit)? = null): (ApiResult<T>) -> Unit {
        return { result ->
            when (result) {
                is ApiResult.Success -> onSuccess?.invoke(result.data)
                is ApiResult.Error -> Log.d(tag, "Error: ${result.message}")
                is ApiResult.Exception -> Log.d(tag, "Exception: ${result.error}")
                is ApiResult.Fail -> Log.d(tag, "Fail: ${result.message}")
            }
        }
    }
}
