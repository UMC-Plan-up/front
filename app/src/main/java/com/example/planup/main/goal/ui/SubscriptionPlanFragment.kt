package com.example.planup.main.goal.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.databinding.FragmentSubscriptionPlanBinding
import com.example.planup.goal.GoalActivity
import com.example.planup.goal.ui.GoalDetailFragment
import com.example.planup.main.MainActivity // MainActivity 클래스 임포트

class SubscriptionPlanFragment : Fragment() {
    private lateinit var binding: FragmentSubscriptionPlanBinding

    private var isFromGoalDetail: Boolean = false
    private var isFromGoalFragment: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSubscriptionPlanBinding.inflate(inflater, container, false)

        isFromGoalDetail = arguments?.getBoolean("IS_FROM_GOAL_DETAIL", false) ?: false
        isFromGoalFragment = arguments?.getBoolean("IS_FROM_GOAL_FRAGMENT", false) ?: false

        clickListener()
        return binding.root
    }

    private fun clickListener() {
        // 뒤로가기 버튼 클릭 시
        binding.backIcon.setOnClickListener {
            if (isFromGoalDetail) {
                requireActivity().setResult(Activity.RESULT_CANCELED)
                requireActivity().finish()
            } else if (isFromGoalFragment) {
                (requireActivity() as? MainActivity)?.navigateToFragment(GoalFragment())
            } else {
                parentFragmentManager.popBackStack()
            }
        }

        // Basic plan 클릭
        binding.basicPlanCard.setOnClickListener {
            resetCardBackgrounds()
            binding.basicPlanCard.setBackgroundResource(R.drawable.bg_plan_basic_selected)
            completeSubscription()
        }

        // Pro plan 클릭
        binding.proPlanCard.setOnClickListener {
            resetCardBackgrounds()
            binding.proPlanCard.setBackgroundResource(R.drawable.bg_plan_card_pro_selected)
            completeSubscription()
        }
    }


    private fun completeSubscription() {
        if (isFromGoalDetail) {
            val resultIntent = Intent()
            resultIntent.putExtra("IS_UNLOCKED", true)
            requireActivity().setResult(Activity.RESULT_OK, resultIntent)
            requireActivity().finish()
        } else if (isFromGoalFragment) {
            val sharedPreferences = requireActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE)
            val nickname = sharedPreferences.getString("nickname", "사용자") ?: "사용자"

            val intent = Intent(requireContext(), GoalActivity::class.java).apply {
                putExtra("start_from_payment_to_goal_detail", true)
                putExtra("goalOwnerName", nickname)

                putExtra("IS_UNLOCKED", true)
            }
            startActivity(intent)

            requireActivity().finish()
        }
    }

    private fun resetCardBackgrounds() {
        binding.basicPlanCard.setBackgroundResource(R.drawable.bg_plan_card)
        binding.proPlanCard.setBackgroundResource(R.drawable.bg_plan_card)
    }
}
