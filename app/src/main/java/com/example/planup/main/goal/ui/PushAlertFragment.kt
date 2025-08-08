package com.example.planup.main.goal.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.databinding.FragmentPushAlertBinding
import com.example.planup.main.goal.item.EditGoalApiResponse
import com.example.planup.main.goal.item.EditGoalRequest
import com.example.planup.main.goal.item.EditGoalResponse
import com.example.planup.main.goal.item.GoalApiService
import com.example.planup.main.goal.item.GoalRetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PushAlertFragment : Fragment() {
    private lateinit var binding: FragmentPushAlertBinding
    private var title: String = ""
    private var oneDose: String = ""
    private var goalId: Long = -1L
    private var authType: String = ""
    private var hour: String = ""
    private var minute: String = ""
    private var second: String = ""
    private var frequency: String = ""
    private var endDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        goalId = arguments?.getLong("goalId") ?: -1L
        title = arguments?.getString("title") ?: ""
        oneDose = arguments?.getString("oneDose") ?: ""
        authType = arguments?.getString("authType") ?: ""
        hour = arguments?.getString("hour") ?: ""
        minute = arguments?.getString("minute") ?: ""
        second = arguments?.getString("second") ?: ""
        frequency = arguments?.getString("frequency") ?: ""

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPushAlertBinding.inflate(inflater, container, false)

        binding.nextButton.setOnClickListener {
            val request = EditGoalRequest(
                goalName = title,
                goalAmount = oneDose,
                goalCategory = "STUDYING",
                goalType = "FRIEND",
                oneDose = 0,
                frequency = frequency.toInt(),
                period = hour,
                endDate = endDate,
                verificationType = "PHOTO",
                limitFriendCount = 3,
                goalTime = hour.toInt() //??
            )
            updateGoal(goalId = goalId, request = request)
        }

        return binding.root
    }

    private fun updateGoal(goalId: Long, request: EditGoalRequest) {
        val apiService = GoalRetrofitInstance.api.create(GoalApiService::class.java)

        apiService.editGoal(goalId, request).enqueue(object : Callback<EditGoalApiResponse<EditGoalResponse>> {
            override fun onResponse(
                call: Call<EditGoalApiResponse<EditGoalResponse>>,
                response: Response<EditGoalApiResponse<EditGoalResponse>>
            ) {
                if (response.isSuccessful && response.body()?.status == 200) {
                    Toast.makeText(requireContext(), "목표 수정 성공", Toast.LENGTH_SHORT).show()
                    // UI 업데이트 또는 화면 이동
                    val nextfragment = FragmentEditGoalComplete()
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.edit_friend_goal_fragment_container, nextfragment)
                        .addToBackStack(null)
                        .commit()
                } else {
                    val errorMessage = response.body()?.message ?: "오류가 발생했습니다."
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<EditGoalApiResponse<EditGoalResponse>>, t: Throwable) {
                Toast.makeText(requireContext(), "에러 발생: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}