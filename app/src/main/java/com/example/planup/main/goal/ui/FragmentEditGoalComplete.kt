package com.example.planup.main.goal.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.planup.R

class FragmentEditGoalComplete : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 레이아웃 연결
        val view = inflater.inflate(R.layout.fragment_edit_goal_complete, container, false)

        // 완료 버튼 클릭 시 Activity 종료
        val completeButton = view.findViewById<Button>(R.id.edit_complete_start_btn)
        completeButton.setOnClickListener {
            requireActivity().finish()
        }

        return view
    }
}
