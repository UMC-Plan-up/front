package com.example.planup.goal

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.planup.databinding.ActivityGoalBinding
import com.example.planup.goal.ui.GoalSelectFragment
import com.example.planup.R

class GoalActivity : AppCompatActivity() {

    lateinit var binding: ActivityGoalBinding
    lateinit var goalOwnerName: String

    var goalName: String = ""
    var goalAmount: String = ""
    var goalCategory: String = ""
    var goalType: String = ""
    var oneDose: String = ""
    var frequency: Int = 0
    var period: String = ""
    var endDate: String = ""
    var verificationType: String = ""
    var limitFriendCount: Int = 0
    var goalTime: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        goalOwnerName = intent.getStringExtra("goalOwnerName") ?: "사용자"

        if (savedInstanceState == null) {
            val first = GoalSelectFragment().apply {
                arguments = (arguments ?: Bundle()).apply {
                    putString("goalOwnerName", goalOwnerName)
                }
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.goal_container, first)
                .commit()
        }
    }

    /** 목표 생성 완료 시점(저장 버튼 클릭)에서 호출 */
    fun completeCreationAndFinish() {
        // 서버에 생성 요청 성공한 뒤 호출하세요.
        finish()  // 결과 전달 없이 종료
    }

    fun navigateToFragment(fragment: Fragment) {
        fragment.arguments = (fragment.arguments ?: Bundle()).apply {
            putString("goalOwnerName", goalOwnerName)
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.goal_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}
