package com.example.planup.goal

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.databinding.ActivityGoalBinding
import com.example.planup.goal.ui.GoalCategoryFragment // GoalSelectFragment를 GoalCategoryFragment로 가정
import com.example.planup.goal.ui.GoalDetailFragment
import com.example.planup.main.MainActivity

class GoalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGoalBinding
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


    private val subscriptionResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val isUnlocked = data?.getBooleanExtra("IS_UNLOCKED", false) ?: false
            if (isUnlocked) {
                Handler(Looper.getMainLooper()).postDelayed({
                    val goalDetailFragment = supportFragmentManager.findFragmentById(R.id.goal_container)
                    if (goalDetailFragment is GoalDetailFragment) {
                        goalDetailFragment.updateLockStatus(true)
                    }
                }, 2000)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        goalOwnerName = intent.getStringExtra("goalOwnerName") ?: "사용자"

        if (savedInstanceState == null) {
            // GoalSelectFragment를 GoalCategoryFragment로 가정
            val first = GoalCategoryFragment().apply {
                arguments = (arguments ?: Bundle()).apply {
                    putString("goalOwnerName", goalOwnerName)
                }
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.goal_container, first)
                .commit()
        }
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

    /* SubscriptionPlanFragment를 시작하고, 결과를 받기 위한 함수 */
    fun startSubscriptionActivity() {
        val intent = Intent(this, MainActivity::class.java)
        // 어떤 경로로 SubscriptionFragment가 호출되었는지 판단
        intent.putExtra("ACTION_NAVIGATE", "SHOW_SUBSCRIPTION")
        intent.putExtra("IS_FROM_GOAL_DETAIL", true)
        subscriptionResultLauncher.launch(intent)
    }
}
