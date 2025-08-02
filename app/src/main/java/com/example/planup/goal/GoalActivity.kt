package com.example.planup.goal
// 회원가입 후 목표설정 플로우

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.databinding.ActivityGoalBinding
import com.example.planup.goal.ui.GoalSelectFragment

class GoalActivity : AppCompatActivity() {

    lateinit var binding: ActivityGoalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nickname = intent.getStringExtra("goalOwnerName") ?: "사용자"


        if (savedInstanceState == null) {

            val goalSelectFragment = GoalSelectFragment().apply {
                arguments = Bundle().apply {
                    putString("goalOwnerName", nickname)
                }
            }

            supportFragmentManager.beginTransaction() //함께 목표 설정 또는 챌린지 설정 선택하는 프레그먼트를 기본 프레그먼트로 사용
                .replace(R.id.goal_container, goalSelectFragment)
                .commitAllowingStateLoss()
        }
    }


    fun navigateToFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.goal_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}