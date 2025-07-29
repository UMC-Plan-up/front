package com.example.planup.goal

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.goal.ui.GoalCategoryFragment

class GoalSettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal_setting)

        if (savedInstanceState == null) {
            val nickname = intent.getStringExtra("goalOwnerName") ?: "사용자"

            val goalCategoryFragment = GoalCategoryFragment().apply {
                arguments = Bundle().apply {
                    putString("goalOwnerName", nickname)
                }
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.goal_setting_container, goalCategoryFragment)
                .commit()
        }
    }

    fun navigateToFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.goal_setting_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}
