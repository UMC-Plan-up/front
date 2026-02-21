package com.example.planup.goal.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.planup.databinding.FragmentGoalCompleteBinding
import com.example.planup.goal.GoalActivity
import com.example.planup.goal.data.GoalCreateRequest
import com.example.planup.goal.util.equil
import com.example.planup.goal.util.goalDataTrue
import com.example.planup.goal.util.setInsets
import com.example.planup.main.goal.viewmodel.GoalViewModel
import com.example.planup.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@AndroidEntryPoint
class GoalCompleteFragment : Fragment() {

    private var _binding: FragmentGoalCompleteBinding? = null
    private val binding get() = _binding!!
    private val ISO_UTC_MILLIS: DateTimeFormatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd")
            .withZone(ZoneOffset.UTC)
    private val viewModel: GoalViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGoalCompleteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setInsets(view)
//        binding.backIcon.setOnClickListener {
//            parentFragmentManager.popBackStack()
//        }

        binding.startPlanUpButton.setOnClickListener {
            sendCreateGoal()
        }
    }

    private fun toApiGoalType(raw: String?) = when (raw?.lowercase()) {
        "friend", "친구" -> "FRIEND"
        "community", "커뮤니티" -> "COMMUNITY"
        "FRIEND" -> raw
        "COMMUNITY" -> raw
        else -> "FRIEND"
    }

    private fun toApiCategory(raw: String?) = when (raw?.lowercase()) {
        "공부하기", "studying", "study" -> "STUDYING"
        "독서하기", "reading", "read"   -> "READING"
        "운동하기", "exercise", "workout"-> "EXERCISING"
        "저축하기", "saving", "save"    -> "SAVING"
        "생활습관", "habit", "lifestyle" -> "LIFESTYLE"
        "취미하기", "hobby"             -> "HOBBY"
        else -> "STUDYING"
    }

    private fun toApiPeriod(raw: String?) = when (raw?.lowercase()) {
        "매일", "daily", "day"     -> "DAY"
        "매주", "weekly", "week"   -> "WEEK"
        "매월", "월간", "monthly", "month" -> "MONTH"
        else -> "DAY"
    }

    private fun toApiVerification(raw: String?) = when (raw?.lowercase()) {
        "타이머", "timer" -> "TIMER"
        "사진", "photo"   -> "PHOTO"
        else -> "TIMER"
    }

    private fun parseOneDose(src: String?): Int =
        src?.filter { it.isDigit() }?.toIntOrNull() ?: 1

    private fun resolveEndDate(endDateArg: String?, periodApi: String): String {
        if (!endDateArg.isNullOrBlank()) {
            return if (endDateArg.contains('T')) {
                endDateArg
            } else {
                val instant = runCatching {
                    LocalDate.parse(endDateArg).atStartOfDay(ZoneOffset.UTC).toInstant()
                }.getOrElse {
                    Instant.now()
                }
                ISO_UTC_MILLIS.format(instant)
            }
        }
        val plusDays = when (periodApi) {
            "DAY"   -> 7
            "WEEK"  -> 28
            "MONTH" -> 30
            else    -> 7
        }
        return ISO_UTC_MILLIS.format(Instant.now().plus(plusDays.toLong(), ChronoUnit.DAYS))
    }

    private fun sendCreateGoal() {
        val goalActivity = requireActivity() as GoalActivity
        if (viewModel.editGoalData !=null && goalDataTrue(viewModel.editGoalData!!)) {
            if (viewModel.goalId != -1)
                viewModel.joinGoal(
                    viewModel.goalId,
                    action = {
                        (requireActivity() as GoalActivity).saveGoalData()

                        goHome()
                    },
                    message = {
                        Toast.makeText(
                            requireContext(),
                            it.ifBlank { "요청 실패" },
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        binding.startPlanUpButton.isEnabled = true
                    }
                )
        }else{
            val goalTypeApi = toApiGoalType(goalActivity.goalType)
            val categoryApi = toApiCategory(goalActivity.goalCategory)
            val periodApi = toApiPeriod(goalActivity.period)
            val verifyApi = toApiVerification(goalActivity.verificationType)
            val endDateApi = resolveEndDate(goalActivity.endDate, periodApi)
            val oneDoseInt = parseOneDose(goalActivity.oneDose)

            val req = GoalCreateRequest(
                goalName = goalActivity.goalName.orEmpty(),
                goalAmount = goalActivity.goalAmount.orEmpty(),
                goalCategory = categoryApi,
                goalType = goalTypeApi,
                oneDose = oneDoseInt,
                frequency = goalActivity.frequency,
                period = periodApi,
                endDate = endDateApi,
                verificationType = verifyApi,
                limitFriendCount = goalActivity.limitFriendCount,
                goalTime = goalActivity.goalTime
            )

            Log.d("GoalDebug", "Final API Request GoalName: ${req.goalName}")
            Log.d("GoalDebug", "Final API Request Body: $req")

//        val prefs = requireContext().getSharedPreferences("userInfo", Context.MODE_PRIVATE)
//        val token = prefs.getString("accessToken", "") ?: ""
//        if (token.isBlank()) {
//            return
//        }

//        val authHeader = "Bearer $token"
            binding.startPlanUpButton.isEnabled = false
            if (viewModel.friendNickname != "사용자" && req.equil(data = viewModel.editGoalData!!)) {

            } else
                viewModel.createGoal(
                    req,
                    action = {
                        (requireActivity() as GoalActivity).saveGoalData()

                        goHome()
                    },
                    message = {
                        Toast.makeText(
                            requireContext(),
                            it.ifBlank { "요청 실패" },
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.startPlanUpButton.isEnabled = true
                    }
                )
        }
//        lifecycleScope.launch {
//            runCatching {
//                withContext(Dispatchers.IO) {
//                    RetrofitInstance.goalApi.createGoal(authHeader, req)
//                }
//            }.onSuccess { resp ->
//                if (resp.isSuccessful && resp.body()?.isSuccess == true) {
//                    (requireActivity() as GoalActivity).saveGoalData()
//
//                    goHome()
//                } else {
//                    val msg = resp.body()?.message ?: resp.errorBody()?.string().orEmpty()
//                    Toast.makeText(
//                        requireContext(),
//                        msg.ifBlank { "요청 실패" },
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    binding.startPlanUpButton.isEnabled = true
//                }
//            }.onFailure { e ->
//                Toast.makeText(
//                    requireContext(),
//                    "네트워크 오류: ${e.message}",
//                    Toast.LENGTH_SHORT
//                ).show()
//                binding.startPlanUpButton.isEnabled = true
//            }
//        }
    }


    private fun goHome() {
        val intent = Intent(requireContext(), MainActivity::class.java).apply {
            putExtra("IS_FROM_GOAL_CREATION", true)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
