package com.example.planup.goal
// 회원가입 후 목표설정 플로우

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.databinding.ActivityGoalBinding
import com.example.planup.goal.ui.GoalCategoryFragment

class GoalActivity : AppCompatActivity() {

    lateinit var binding: ActivityGoalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGoalBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val nickname = intent.getStringExtra("goalOwnerName") ?: "사용자"

        if (savedInstanceState == null) {
            // GoalCategoryFragment로 이동하면서 nickname 전달
            val goalCategoryFragment = GoalCategoryFragment().apply {
                arguments = Bundle().apply {
                    putString("goalOwnerName", nickname)

                }
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.goal_container, goalCategoryFragment)
                .commit()
        }
    }


    fun navigateToFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.goal_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}
