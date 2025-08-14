package com.example.planup.main.goal.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.databinding.FragmentSubscriptionPlanBinding
import com.example.planup.main.MainActivity
import com.example.planup.goal.ui.GoalDetailFragment
import com.example.planup.signup.SignupActivity // ⭐ SignupActivity에서 닉네임을 가져오기 위해 import

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
            (requireActivity() as MainActivity).supportFragmentManager.beginTransaction()
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
        val nicknameFromSignup = (activity as? SignupActivity)?.nickname
        val goalOwnerName = nicknameFromSignup?.takeIf { it.isNotBlank() } ?: "사용자"

        val goalDetailFragment = GoalDetailFragment().apply {
            arguments = Bundle().apply {
                putBoolean("HIDE_GOAL_CONTAINER", true)
                putString("goalOwnerName", goalOwnerName)
            }
        }

        binding.root.postDelayed({
            (requireActivity() as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, goalDetailFragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }, 1500L)
    }

    private fun resetCardBackgrounds() {
        binding.basicPlanCard.setBackgroundResource(R.drawable.bg_plan_card)
        binding.proPlanCard.setBackgroundResource(R.drawable.bg_plan_card)
    }
}
