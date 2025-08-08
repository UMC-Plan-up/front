package com.example.planup.main.goal.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.databinding.FragmentSubscriptionPlanBinding
import com.example.planup.main.MainActivity

class SubscriptionPlanFragment : Fragment(){
    lateinit var binding: FragmentSubscriptionPlanBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{
        binding = FragmentSubscriptionPlanBinding.inflate(inflater, container, false)
        clickListener()
        return binding.root
    }

    private fun clickListener(){
        binding.backIcon.setOnClickListener{
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, GoalFragment())
                .commitAllowingStateLoss()
        }
    }
}