package com.example.planup.main.goal.ui

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.planup.R
import com.example.planup.databinding.FragmentPushAlertBinding
import com.example.planup.main.goal.item.EditGoalRequest
import com.example.planup.main.goal.item.GoalApiService
import com.example.planup.main.goal.item.GoalRetrofitInstance
import kotlinx.coroutines.launch

class PushAlertFragment : Fragment() {
    private lateinit var binding: FragmentPushAlertBinding
    private lateinit var prefs: SharedPreferences
    private var goalId: Int = 0
    private var goalName: String = ""
    private var goalAmount: String = ""
    private var goalCategory: String = ""
    private var goalType: String = ""
    private var oneDose: Int = 0
    private var frequency: Int = 0
    private var period: String = ""
    private var endDate: String = ""
    private var verificationType: String = ""
    private var limitFriendCount: Int = 0
    private var goalTime: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        goalId = arguments?.getInt("goalId") ?: 0
        goalName = arguments?.getString("goalName") ?: ""
        goalAmount = arguments?.getString("goalAmount") ?: ""
        goalCategory = arguments?.getString("goalCategory") ?: ""
        goalType = arguments?.getString("goalType") ?: ""
        oneDose = arguments?.getInt("oneDose") ?: 0
        frequency = arguments?.getInt("frequency") ?: 0
        period = arguments?.getString("period") ?: ""
        endDate = arguments?.getString("endDate") ?: ""
        verificationType = arguments?.getString("verificationType") ?: ""
        limitFriendCount = arguments?.getInt("limitFriendCount") ?: 0
        goalTime = arguments?.getInt("goalTime") ?: 0
        prefs = requireActivity().getSharedPreferences("userInfo", MODE_PRIVATE)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPushAlertBinding.inflate(inflater, container, false)

        binding.alertBackIv.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.nextButton.setOnClickListener {
            val request = EditGoalRequest(
                goalName = goalName,
                goalAmount = goalAmount,
                goalCategory = goalCategory,
                goalType = goalType,
                oneDose = oneDose,
                frequency = frequency,
                period = period,
                endDate = endDate,
                verificationType = verificationType,
                limitFriendCount = limitFriendCount,
                goalTime = goalTime
            )
            updateGoal(goalId = goalId, request = request)
            val nextfragment = FragmentEditGoalComplete()
            val bundle = Bundle().apply {
                putString("goalId", goalId.toString())
            }
            nextfragment.arguments = bundle
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.edit_friend_goal_fragment_container, nextfragment)
                .addToBackStack(null)
                .commit()
        }

        return binding.root
    }

    private fun updateGoal(goalId: Int, request: EditGoalRequest) {
        lifecycleScope.launch {
            try {
                val apiService = GoalRetrofitInstance.api.create(GoalApiService::class.java)
                val token = prefs.getString("accessToken", null)
                val response = apiService.editGoal(token = "Bearer $token", goalId = goalId, request)
                if (response.isSuccess){
                    val nextfragment = FragmentEditGoalComplete()
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.edit_friend_goal_fragment_container, nextfragment)
                        .addToBackStack(null)
                        .commit()
                } else {
                    val errorMessage = response.message
                    Log.d("EditGoalFragment", "에러 메시지: $errorMessage")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("EditGoalFragment", "네트워크 오류: ${e.localizedMessage}")
            }
        }
    }
}
