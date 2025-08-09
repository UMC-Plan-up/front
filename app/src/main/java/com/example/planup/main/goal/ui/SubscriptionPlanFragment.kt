package com.example.planup.main.goal.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.databinding.FragmentSubscriptionPlanBinding
import com.example.planup.main.MainActivity
import android.widget.Toast

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

    /* 뒤로가기 아이콘 → 이전 화면으로 이동 */
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
        }

        // Pro plan 클릭
        binding.proPlanCard.setOnClickListener {
            resetCardBackgrounds()
            binding.proPlanCard.setBackgroundResource(R.drawable.bg_plan_card_pro_selected)
        }
    }

    /* 모든 카드 배경 원상복구 */
    private fun resetCardBackgrounds() {
        binding.basicPlanCard.setBackgroundResource(R.drawable.bg_plan_card)
        binding.proPlanCard.setBackgroundResource(R.drawable.bg_plan_card)
    }
}
