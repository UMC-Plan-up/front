package com.example.planup.main.goal.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.databinding.FragmentSubscriptionPlanBinding
import com.example.planup.main.MainActivity

class SubscriptionPlanFragment : Fragment() {
    private lateinit var binding: FragmentSubscriptionPlanBinding

    private var isFromGoalDetail: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSubscriptionPlanBinding.inflate(inflater, container, false)
        isFromGoalDetail = arguments?.getBoolean("IS_FROM_GOAL_DETAIL", false) ?: false

        clickListener()
        return binding.root
    }

    private fun clickListener() {
        binding.backIcon.setOnClickListener {
            if (isFromGoalDetail) {
                parentFragmentManager.popBackStack()
            } else {
                (requireActivity() as MainActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.main_container, GoalFragment())
                    .commitAllowingStateLoss()
            }
        }

        // Basic plan 클릭
        binding.basicPlanCard.setOnClickListener {
            resetCardBackgrounds()
            binding.basicPlanCard.setBackgroundResource(R.drawable.bg_plan_basic_selected)
            sendResultAndPopBackStack(true)
        }

        // Pro plan 클릭
        binding.proPlanCard.setOnClickListener {
            resetCardBackgrounds()
            binding.proPlanCard.setBackgroundResource(R.drawable.bg_plan_card_pro_selected)
            sendResultAndPopBackStack(true)
        }
    }


    private fun sendResultAndPopBackStack(isPlanSelected: Boolean) {
        // 결과 Bundle 생성
        val result = Bundle().apply {
            putBoolean("is_plan_selected", isPlanSelected)
        }

        parentFragmentManager.setFragmentResult("subscription_result_key", result)

        parentFragmentManager.popBackStack()
    }

    private fun resetCardBackgrounds() {
        binding.basicPlanCard.setBackgroundResource(R.drawable.bg_plan_card)
        binding.proPlanCard.setBackgroundResource(R.drawable.bg_plan_card)
    }
}