package com.example.planup.main.goal.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.databinding.FragmentSubscriptionPlanBinding

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
                requireActivity().setResult(Activity.RESULT_CANCELED)
                requireActivity().finish()
            } else {
                (requireActivity() as com.example.planup.main.MainActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.main_container, GoalFragment())
                    .commitAllowingStateLoss()
            }
        }

        // Basic plan 클릭
        binding.basicPlanCard.setOnClickListener {
            resetCardBackgrounds()
            binding.basicPlanCard.setBackgroundResource(R.drawable.bg_plan_basic_selected)
            completeSubscriptionAndReturn()
        }

        // Pro plan 클릭
        binding.proPlanCard.setOnClickListener {
            resetCardBackgrounds()
            binding.proPlanCard.setBackgroundResource(R.drawable.bg_plan_card_pro_selected)
            completeSubscriptionAndReturn()
        }
    }


    private fun completeSubscriptionAndReturn() {
        val resultIntent = Intent()
        resultIntent.putExtra("IS_UNLOCKED", true)

        // MainActivity를 종료하고 GoalActivity로 결과를 전달
        requireActivity().setResult(Activity.RESULT_OK, resultIntent)
        requireActivity().finish()
    }

    private fun resetCardBackgrounds() {
        binding.basicPlanCard.setBackgroundResource(R.drawable.bg_plan_card)
        binding.proPlanCard.setBackgroundResource(R.drawable.bg_plan_card)
    }
}
