package com.planup.planup.main.home.ui

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.planup.planup.R
import com.planup.planup.databinding.FragmentFriendGoalListBinding
import com.planup.planup.main.MainActivity
import com.planup.planup.main.goal.item.FriendGoalListResult
import com.planup.planup.main.goal.item.GoalApiService
import com.planup.planup.main.goal.item.GoalRetrofitInstance
import com.planup.planup.main.home.adapter.FriendGoalListAdapter
import com.planup.planup.main.home.adapter.FriendGoalWithAchievement
import com.planup.planup.network.RetrofitInstance
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.time.LocalDate

class tempFriendGoalListFragment : Fragment() {

    // binding은 nullable로 관리
    private var _binding: FragmentFriendGoalListBinding? = null
    private val binding get() = _binding!!

    private lateinit var goalAdapter: FriendGoalListAdapter
    private var friendId: Int = 0
    private lateinit var friendName: String
    private lateinit var friendProfileImage: String
    private val sampleItems = mutableListOf(
        FriendGoalWithAchievement(
            goalId = 1,
            goalName = "헬스장 가기",
            goalType = "FRIEND",
            goalAmount = "헬스장 가서 30분 채우고 오기",
            verificationType = "TIMER",
            goalTime = 400,
            frequency = 1,
            oneDose = 1,
            totalAchievement = 85
        ),
        FriendGoalWithAchievement(
            goalId = 2,
            goalName = "물 마시기",
            goalType = "FRIEND",
            goalAmount = "벌컥벌컥~",
            verificationType = "PHOTO",
            goalTime = 400,
            frequency = 1,
            oneDose = 1,
            totalAchievement = 70
        )
    )
    private var goalList = mutableListOf<FriendGoalListResult>(
    )
    private lateinit var prefs: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendGoalListBinding.inflate(inflater, container, false)
        friendId = arguments?.getInt("friendId") ?: 0
        friendName = arguments?.getString("friendName") ?: ""
        friendProfileImage = arguments?.getString("friendResId") ?: ""

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = (context as MainActivity).getSharedPreferences("userInfo", MODE_PRIVATE)
        val token = prefs.getString("accessToken", null)

        Glide.with(this).load(friendProfileImage)
            .circleCrop().into(binding.friendGoalListProfileIv)

        loadTodayAchievement(token)


        binding.friendGoalListTv.text = "${friendName}의 목표 리스트"
        loadFriendGoalList(token = "Bearer $token", friendId = friendId.toInt())

        viewLifecycleOwner.lifecycleScope.launch {
            val goals = loadFriendGoalsWithAchievement(token, friendId)
            for (goal in goals) {
                sampleItems.add(goal)
            }
            Log.d("FriendGoal", "최종 goals 데이터: $sampleItems")

            goalAdapter = FriendGoalListAdapter(sampleItems) { item ->
                val detailFragment = FriendGoalDetailFragment()
                val bundle = Bundle().apply {
                    putInt("friendId", friendId)
                    putInt("goalId", item.goalId)
                    putString("title", item.goalName)
                }
                detailFragment.arguments = bundle

                parentFragmentManager.beginTransaction()
                    .replace(R.id.main_container, detailFragment)
                    .addToBackStack(null)
                    .commit()
            }

            binding.friendGoalListRv.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = goalAdapter
            }
        }

        binding.friendGoalListBackIv.setOnClickListener {
            parentFragmentManager.popBackStack()
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // 메모리 누수 방지
    }

    private fun loadFriendGoalList(token: String, friendId: Int) {
        lifecycleScope.launch {
            try {
                val apiService = GoalRetrofitInstance.api.create(GoalApiService::class.java)
                val response = apiService.getFriendGoalList(token = "Bearer $token", friendId = 155)
                if (response.isSuccess) {
                    for(item in response.result) {
                        goalList.add(item)
                    }
                    Log.d("FriendGoalListFragmentApi", "loadFriendGoalList 호출 성공: ${goalList}")
                    goalAdapter.notifyDataSetChanged()
                } else {
                    Log.d("FriendGoalListFragmentApi", "loadFriendGoalList 호출 실패: ${response.message}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("FriendGoalListFragmentApi", "loadFriendGoalList 오류: ${e.message}")
            }
        }
    }

    suspend fun loadFriendGoalsWithAchievement(
        token: String?,
        friendId: Int
    ): List<FriendGoalWithAchievement> {
        val results = mutableListOf<FriendGoalWithAchievement>()

        try {
            Log.d("FriendGoal", "요청 시작: friendId=$friendId")

            // 1. 친구 목표 리스트 가져오기
            val goalService = GoalRetrofitInstance.api.create(GoalApiService::class.java)
            val goalListResponse = goalService.getFriendGoalList("Bearer $token", friendId)
            Log.d("FriendGoal", "getFriendGoalList(friendId=$friendId) response: $goalListResponse")

            if (goalListResponse.isSuccess) {
                val goalList = goalListResponse.result
                Log.d("FriendGoal", "friendId=$friendId 목표 개수=${goalList.size}")

                for (goal in goalList) {
                    try {
                        Log.d("FriendGoal", "목표 처리 중: goalId=${goal.goalId}, goalName=${goal.goalName}")

                        // 2. 각 goalId의 달성률 가져오기
                        val achievementResponse = goalService.getFriendGoalAchievement(
                            "Bearer $token",
                            friendId,
                            goal.goalId
                        )
                        Log.d(
                            "FriendGoal",
                            "getFriendGoalAchievement(friendId=$friendId, goalId=${goal.goalId}) response: $achievementResponse"
                        )

                        if (achievementResponse.isSuccess) {
                            val achievement = achievementResponse.result.totalAchievement
                            Log.d(
                                "FriendGoal",
                                "달성률 성공: friendId=$friendId, goalId=${goal.goalId}, achievement=$achievement"
                            )

                            // 3. 결과 리스트에 추가
                            results.add(
                                FriendGoalWithAchievement(
                                    goal = goal,
                                    totalAchievement = achievement
                                )
                            )
                        } else {
                            Log.e(
                                "FriendGoal",
                                "달성률 실패: friendId=$friendId, goalId=${goal.goalId}, message=${achievementResponse.message}"
                            )
                        }
                    } catch (e: Exception) {
                        Log.e("FriendGoal", "달성률 API 예외: goalId=${goal.goalId}, error=${e.message}")
                    }
                }
            } else {
                Log.e(
                    "FriendGoal",
                    "목표 리스트 실패: friendId=$friendId, message=${goalListResponse.message}"
                )
            }
        } catch (e: Exception) {
            Log.e("FriendGoal", "전체 처리 예외: ${e.message}")
            e.printStackTrace()
        }

        Log.d("FriendGoal", "최종 결과 friendId=$friendId -> ${results.size}개 목표 정리됨")
        return results
    }

    private fun loadTodayAchievement(token: String?) {
        if(token.isNullOrEmpty()) {
            Log.e("loadTodayAchievement", "Token is null or empty")
            return
        }

        lifecycleScope.launch {
            try {
                val apiService = RetrofitInstance.goalApi
                val today = LocalDate.now() // yyyy-MM-dd

                val response = apiService.getDailyAchievement(
                    targetDate = today.toString()
                )

                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    val dailyPieChart = binding.friendDailyGoalCompletePc
                    val achievementRate = response.body()?.result?.achievementRate!!
                    binding.friendDailyGoalPercentTv.text = "$achievementRate%"
                    setupPieChart(dailyPieChart, achievementRate)
                } else {
                    Log.d("GoalFragmentApi", "loadTodayAchievement 실패: ${response.message()}")
                }

            } catch (e: Exception) {
                e.printStackTrace()
                if (e is HttpException) {
                    Log.e("todayachievement", "Http error: ${e.code()} ${e.response()?.errorBody()?.string()}")
                } else {
                    Log.e("todayachievement", "Other error: ${e.message}", e)
                }
            }
        }
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
