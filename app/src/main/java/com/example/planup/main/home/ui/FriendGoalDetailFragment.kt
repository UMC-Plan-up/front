package com.example.planup.main.home.ui


import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.planup.main.goal.item.GoalApiService
import com.example.planup.main.goal.item.GoalRetrofitInstance
import kotlinx.coroutines.launch
import retrofit2.HttpException
import com.example.planup.R
import com.example.planup.main.goal.item.CreateCommentRequest
import com.example.planup.main.home.adapter.PhotoAdapter
import com.example.planup.network.ApiResult
import com.example.planup.network.RetrofitInstance

class FriendGoalDetailFragment : Fragment() {

    private var _binding: FragmentFriendGoalDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var chart: CombinedChart
    private lateinit var userPrefs: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFriendGoalDetailBinding.inflate(inflater, container, false)
        userPrefs = requireContext().getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        val token = userPrefs.getString("accessToken", null)

        val goalId = arguments?.getInt("goalId") ?: 0
        val title = arguments?.getString("title") ?: ""
        val friendId = arguments?.getInt("friendId") ?: 0

        binding.friendDetailTitleTv.text = title
        binding.friendDetailWeekFocusTv.text = getString(R.string.friend_detail_week_focus, title)
        binding.friendGoalDetailTodayfocusTv.text = getString(R.string.friend_goal_detail_today_text, title)
        loadComment(goalId)
        loadTodayFriendTime(token, friendId, goalId)
        loadFriendPhotos(friendId, goalId)

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



        binding.friendGoalSendCommentIv.setOnClickListener {
            val comment = binding.friendGoalCommentEt.text.toString()
            if (comment.isNotEmpty()) {
                sendComment(goalId, comment)
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

    private fun loadComment(goalId: Int) {
        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.goalApi.getComments(goalId = goalId)
                if(response.isSuccess) {
                    binding.friendGoalCommentTv.text = response.result[0].content
                    binding.friendGoalOtherNicknameTv.text = response.result[0].writerNickname
                    Glide.with(this@FriendGoalDetailFragment)
                        .load(response.result[0].writerProfileImg)
                        .error(R.drawable.img_friend_profile_sample1)
                        .into(binding.friendGoalOtherProfileIv)
                } else {
                    Log.d("FriendGoalDetailFragment", "loadComment 실패: ${response.message}")
                    //binding.friendGoalOtherCommentLl.visibility = View.GONE
                }
            } catch(e: Exception) {
                Log.d("FriendGoalDetailFragment", "loadComment 오류: ${e.message}")
                //binding.friendGoalOtherCommentLl.visibility = View.GONE
            }
        }
    }

    private fun loadTodayFriendTime(token: String?, friendId: Int, goalId: Int){
        lifecycleScope.launch {
            try {
                val apiService = RetrofitInstance.verificationApi
                val response = apiService.getTodayFriendTimer(
                    token = "Bearer $token",
                    friendId = friendId,
                    goalId = goalId
                )
                if (response.isSuccessful) {
                    binding.goalDetailTodayTimerTv.text = response.body()?.result?.formattedTime
                } else {
                    Log.d("FriendGoalDetailFragment", "loadTodayFriendTime 실패: ${response.message()}")
                }
            } catch (e: Exception){
                Log.d("FriendGoalDetailFragment", "loadTodayFriendTime 오류: ${e.message}")
            }
        }
    }

    private fun loadFriendPhotos(friendId: Int, goalId: Int) {
        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.goalApi.getFriendPhotos(
                    friendId = friendId,
                    goalId = goalId,
                    userId = userPrefs.getInt("userId", 0)
                )
                if (response.isSuccess) {
                    val photoUrls = response.result.map { it.photoImg } // result 안에 url 속성이 있다고 가정
                    setupRecyclerView(photoUrls)
                }
            } catch (e: Exception){

            }
        }

    }
    private fun setupRecyclerView(photoUrls: List<String>) {
        val recyclerView = binding.photoRecyclerView
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 4) // 4열
        recyclerView.adapter = PhotoAdapter(photoUrls)
    }

    private fun sendComment(goalId: Int, comment: String) {
        lifecycleScope.launch {
            try {
                val request = CreateCommentRequest(content = comment, parentCommentId = 0, reply = false)
                val goalService = RetrofitInstance.goalApi
                val response = goalService.createComment(goalId = goalId, comment = request)
                if(response.isSuccess){
                    binding.friendGoalCommentEt.text.clear()
                    loadComment(goalId)
                } else {
                    Log.d("FriendGoalDetailFragment", "sendComment 실패: ${response.message}")
                }
            } catch (e: Exception) {
                if(e is HttpException) Log.d("FriendGoalDetailFragment", "sendComment 오류: ${e.code()} ${e.message()}")
                else Log.d("FriendGoalDetailFragment", "sendComment 오류: ${e.message}")
            }

        }
    }
}
