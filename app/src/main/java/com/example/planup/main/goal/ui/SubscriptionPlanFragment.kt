package com.example.planup.main.goal.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.databinding.FragmentSubscriptionPlanBinding
import com.example.planup.main.MainActivity
import androidx.core.os.postDelayed
import com.example.planup.goal.ui.GoalDetailFragment

class SubscriptionPlanFragment : Fragment() {
    private lateinit var binding: FragmentSubscriptionPlanBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSubscriptionPlanBinding.inflate(inflater, container, false)
        clickListener()
        return binding.root
    }

    private fun clickListener() {
        binding.backIcon.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, GoalFragment())
                .commitAllowingStateLoss()
        }

        // Basic plan 클릭
        binding.basicPlanCard.setOnClickListener {
            resetCardBackgrounds()
            binding.basicPlanCard.setBackgroundResource(R.drawable.bg_plan_basic_selected)
            navigateToGoalDetailFragment()
        }

        // Pro plan 클릭
        binding.proPlanCard.setOnClickListener {
            resetCardBackgrounds()
            binding.proPlanCard.setBackgroundResource(R.drawable.bg_plan_card_pro_selected)
            navigateToGoalDetailFragment()
        }
    }

    private fun navigateToGoalDetailFragment() {
        // GoalDetailFragment로 이동하기 전에 Bundle에 데이터 추가
        val goalDetailFragment = GoalDetailFragment().apply {
            arguments = Bundle().apply {
                // 이 화면에서 넘어왔을 때 goalContainer를 숨기도록 설정
                putBoolean("HIDE_GOAL_CONTAINER", true)
            }
        }

        binding.root.postDelayed({
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, goalDetailFragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }, 2000L)
    }

    private fun resetCardBackgrounds() {
        binding.basicPlanCard.setBackgroundResource(R.drawable.bg_plan_card)
        binding.proPlanCard.setBackgroundResource(R.drawable.bg_plan_card)
    }
}