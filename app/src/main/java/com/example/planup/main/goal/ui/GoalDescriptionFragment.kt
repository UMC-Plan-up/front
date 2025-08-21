package com.example.planup.main.goal.ui

import android.app.Dialog
import android.content.Context.MODE_PRIVATE
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.planup.R
import com.example.planup.databinding.FragmentGoalDescriptionBinding
import com.example.planup.main.MainActivity
import com.example.planup.main.goal.item.GoalApiService
import com.example.planup.main.goal.item.GoalRetrofitInstance
import kotlinx.coroutines.launch

class GoalDescriptionFragment : Fragment() {

    companion object { const val ARG_GOAL_ID = "goalId" }

    private lateinit var binding: FragmentGoalDescriptionBinding
    private var isPublic: Boolean = true
    private var goalId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        goalId = arguments?.getInt(ARG_GOAL_ID, -1) ?: -1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGoalDescriptionBinding.inflate(inflater, container, false)

        binding.btnBack.setOnClickListener { parentFragmentManager.popBackStack() }
        binding.btnPublic.setOnClickListener {
            if (!isPublic) { isPublic = true; applyToggleUI(isPublic) }
        }
        binding.btnPrivate.setOnClickListener { if (isPublic) showPrivateDialog() }

        if (goalId <= 0) {
            Toast.makeText(requireContext(), "잘못된 목표입니다.", Toast.LENGTH_SHORT).show()
        } else {
            loadGoalDetail(goalId)   // ✅ 주석 해제해서 실제 호출
        }

        return binding.root
    }

    private fun loadGoalDetail(goalId: Int) {
        val prefs = (requireActivity() as MainActivity).getSharedPreferences("userInfo", MODE_PRIVATE)
        val token = prefs.getString("accessToken", null)
        if (token.isNullOrBlank()) {
            Toast.makeText(requireContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            runCatching {
                val api = GoalRetrofitInstance.api.create(GoalApiService::class.java)
                api.getGoalDetail(token = "Bearer $token", goalId = goalId) // GoalDetailResponse
            }.onSuccess { resp ->
                if (resp.isSuccess) {
                    val goal: com.example.planup.main.goal.item.GoalResult = resp.result  // ✅ 타입 맞춤
                    bindGoal(goal)
                } else {
                    Toast.makeText(requireContext(), resp.message, Toast.LENGTH_SHORT).show()
                }
            }.onFailure {
                Toast.makeText(requireContext(), "목표 정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 현재 서버 DTO(GoalResult)에 존재하는 필드만 안전하게 바인딩
    private fun bindGoal(goal: com.example.planup.main.goal.item.GoalResult) {
        binding.goalTitleTv.text = goal.goalName
        binding.oneDoseTv.text = "${goal.oneDose}"

        // 아래 값들은 GoalResult에 아직 없으므로 일단 표시 보류/임시값
        binding.periodTv.text = "-"
        binding.frequencyTv.text = "-"
        binding.verificationTv.text = "-"

        // 참여자 수 문구가 "%d명 참여중" 형식이면 strings.xml에
        // <string name="goal_description_attendee_cnt_fmt">%1$d명 참여중</string>
        // 로 추가하고 아래처럼 사용하세요. (현재는 데이터가 없으니 임시 0)
        // binding.goal_description_attendee_cnt_tv.text =
        //     getString(R.string.goal_description_attendee_cnt_fmt, 0)

        isPublic = goal.public
        applyToggleUI(isPublic)
    }

    private fun applyToggleUI(publicSelected: Boolean) {
        binding.btnPublic.isSelected = publicSelected
        binding.btnPrivate.isSelected = !publicSelected
        binding.btnPublic.setTextColor(if (publicSelected) Color.WHITE else Color.BLACK)
        binding.btnPrivate.setTextColor(if (publicSelected) Color.BLACK else Color.WHITE)
    }

    private fun showPrivateDialog() {
        val dialog = Dialog(requireContext()).apply {
            setContentView(R.layout.dialog_private_goal)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
        }
        dialog.findViewById<ImageView>(R.id.popup_block_no_iv)?.setOnClickListener { dialog.dismiss() }
        dialog.findViewById<ImageView>(R.id.popup_block_yes_iv)?.setOnClickListener {
            isPublic = false
            applyToggleUI(isPublic)
            dialog.dismiss()
        }
        dialog.show()
    }
}