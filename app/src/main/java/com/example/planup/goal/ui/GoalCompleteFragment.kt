package com.example.planup.goal.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.planup.databinding.FragmentGoalCompleteBinding
import com.example.planup.goal.data.GoalCreateRequest
import com.example.planup.main.MainActivity
import com.example.planup.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class GoalCompleteFragment : Fragment() {

    private var _binding: FragmentGoalCompleteBinding? = null
    private val binding get() = _binding!!
    private var goalOwnerName: String? = null
    private var goalType: String? = null
    private var goalCategory: String? = null
    private var goalName: String? = null
    private var goalAmount: String? = null
    private var verificationType: String? = null
    private var period: String? = null
    private var frequency: Int = 0
    private var limitFriendCount: Int = 0
    private var goalTime: Int = 0
    private val ISO_UTC_MILLIS: DateTimeFormatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .withZone(ZoneOffset.UTC)

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

        arguments?.let { args ->
            goalOwnerName     = args.getString("goalOwnerName")
            goalType          = args.getString("goalType")
            goalCategory      = args.getString("goalCategory")
            goalName          = args.getString("goalName")
            goalAmount        = args.getString("goalAmount")
            verificationType  = args.getString("verificationType")
            period            = args.getString("period")
            frequency         = args.getInt("frequency", 0)
            limitFriendCount  = args.getInt("limitFriendCount", 0)
            goalTime          = args.getInt("goalTime", 0)
        }

        binding.backIcon.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.startPlanUpButton.setOnClickListener {
            sendCreateGoal()
        }
    }

    private fun toApiGoalType(raw: String?) = when (raw?.lowercase()) {
        "friend", "친구" -> "FRIEND"
        "community", "커뮤니티" -> "COMMUNITY"
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
        val goalTypeApi  = toApiGoalType(goalType)
        val categoryApi  = toApiCategory(goalCategory)
        val periodApi    = toApiPeriod(period)
        val verifyApi    = toApiVerification(verificationType)

        val endDateFromArg = arguments?.getString("endDate")
        val endDateApi     = resolveEndDate(endDateFromArg, periodApi)
        val oneDoseInt     = parseOneDose(goalAmount)

        val req = GoalCreateRequest(
            goalName         = goalName.orEmpty(),
            goalAmount       = goalAmount.orEmpty(),
            goalCategory     = categoryApi,
            goalType         = goalTypeApi,
            oneDose          = oneDoseInt,
            frequency        = frequency,
            period           = periodApi,
            endDate          = endDateApi,
            verificationType = verifyApi,
            limitFriendCount = limitFriendCount,
            goalTime         = goalTime
        )

        val prefs = requireContext().getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        val token = prefs.getString("accessToken", "") ?: ""
        if (token.isBlank()) {
            return
        }

        val authHeader = "Bearer $token"
        binding.startPlanUpButton.isEnabled = false

        lifecycleScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    RetrofitInstance.goalApi.createGoal(authHeader, req)
                }
            }.onSuccess { resp ->
                if (resp.isSuccessful && resp.body()?.isSuccess == true) {
                    goHome()
                } else {
                    val msg = resp.body()?.message ?: resp.errorBody()?.string().orEmpty()
                    Toast.makeText(
                        requireContext(),
                        msg.ifBlank { "요청 실패" },
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.startPlanUpButton.isEnabled = true
                }
            }.onFailure { e ->
                Toast.makeText(
                    requireContext(),
                    "네트워크 오류: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                binding.startPlanUpButton.isEnabled = true
            }
        }
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
