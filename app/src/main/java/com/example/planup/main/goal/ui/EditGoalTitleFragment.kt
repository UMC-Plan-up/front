package com.example.planup.main.goal.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.planup.R
import com.example.planup.databinding.FragmentEditGoalTitleBinding
import com.example.planup.main.goal.item.EditGoalResponse
import com.example.planup.main.goal.item.GoalRetrofitInstance
import com.example.planup.main.goal.item.GoalApiService
import kotlinx.coroutines.launch

class EditGoalTitleFragment : Fragment() {
    private lateinit var binding: FragmentEditGoalTitleBinding

    private var goalId: Int = 0
    private lateinit var prefs: SharedPreferences
    private var goalData: EditGoalResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // arguments에서 goalId 받아오기
        goalId = arguments?.getInt("goalId") ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditGoalTitleBinding.inflate(inflater, container, false)
        val view = inflater.inflate(R.layout.fragment_edit_goal_title, container, false)
        prefs = requireActivity().getSharedPreferences("userInfo", android.content.Context.MODE_PRIVATE)
        val token = prefs.getString("accessToken", null)
        getGoalEditData(token, goalId)

        val titleEt = binding.editFriendGoalNameEt
        val goalAmountEt = binding.editFriendGoalPeriodEt
        val nextBtn = binding.editFriendGoalNextBtn

        nextBtn.setOnClickListener {
            goalData?.let { goal ->
                val goalName = binding.editFriendGoalNameEt.text.toString()
                val goalAmount = binding.editFriendGoalPeriodEt.text.toString()

                val bundle = Bundle().apply {
                    putInt("goalId", goalId)
                    putString("goalName", goalName)
                    putInt("oneDose", goal.oneDose)
                    putString("goalCategory", goal.goalCategory)
                    putString("goalType", goal.goalType)
                    putString("period", goal.period)
                    putString("endDate", goal.endDate)
                    putString("verificationType", goal.verificationType)
                    putInt("limitFriendCount", goal.limitFriendCount)
                    putInt("goalTime", goal.goalTime)
                    putInt("frequency", goal.frequency)
                    putString("goalAmount", goalAmount)
                }

                val nextFragment = EditGoalTimerFragment()
                nextFragment.arguments = bundle

                parentFragmentManager.beginTransaction()
                    .replace(R.id.edit_friend_goal_fragment_container, nextFragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
        return view
    }

    private fun getGoalEditData(token: String?, goalId: Int) {
        lifecycleScope.launch {
            try {
                val apiService = GoalRetrofitInstance.api.create(GoalApiService::class.java)
                val response = apiService.getEditGoal(token = "Bearer $token", goalId = goalId)
                if (response.isSuccess) {
                    goalData = response.result
                    // 기존 값으로 EditText 초기화
                    binding.editFriendGoalNameEt.setText(goalData?.goalName)
                    binding.editFriendGoalPeriodEt.setText(goalData?.oneDose.toString())
                } else {
                    Log.d("EditGoalTitleFragment", "API 실패: ${response.message}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("EditGoalTitleFragment", "네트워크 오류 Exception: $e")
            }
        }
    }
}
