package com.example.planup.main.goal.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.databinding.FragmentEditGoalCompleteBinding

class FragmentEditGoalComplete : Fragment() {
    private lateinit var binding: FragmentEditGoalCompleteBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 레이아웃 연결
        binding = FragmentEditGoalCompleteBinding.inflate(inflater, container, false)
        val view = inflater.inflate(R.layout.fragment_edit_goal_complete, container, false)

        val goalId = arguments?.getString("goalId")
        Log.d("FragmentEditGoalComplete", "goalId: $goalId")
        // 완료 버튼 클릭 시 Activity 종료
        val completeButton = binding.editCompleteStartBtn
        completeButton.setOnClickListener {
            // activity를 EditFriendGoalActivity로 캐스팅
            (activity as? EditFriendGoalActivity)?.onGoalEditComplete(true)
        }

        return view
    }
}
