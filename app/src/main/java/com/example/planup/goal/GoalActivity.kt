package com.example.planup.goal

import android.app.Activity
import android.content.Context
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
import com.example.planup.goal.ui.PushAlertFragment
import com.example.planup.goal.ui.GoalSelectFragment
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

    var notificationEnabled: Boolean = false
    var regularAlertEnabled: Boolean = false
    var alertTimeOfDay: String = ""
    var alertHour: String = ""
    var alertMinute: String = ""
    var alertDays: MutableSet<String> = mutableSetOf()

    private val subscriptionResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val isUnlocked = data?.getBooleanExtra("IS_UNLOCKED", false) ?: false
            if (isUnlocked) {
                Handler(Looper.getMainLooper()).postDelayed({
                    val goalDetailFragment =
                        supportFragmentManager.findFragmentById(R.id.goal_container)
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

        loadLastGoalData()

        if (savedInstanceState == null) {
            val isFromPayment = intent.getBooleanExtra("start_from_payment", false)
            val isFromPaymentToDetail =
                intent.getBooleanExtra("start_from_payment_to_goal_detail", false)

            val startFragment = when {
                isFromPayment -> PushAlertFragment()
                isFromPaymentToDetail -> GoalDetailFragment().apply {
                    arguments = Bundle().apply {
                        putBoolean("IS_UNLOCKED_FROM_SUBSCRIPTION", true)
                        putString("goalOwnerName", goalOwnerName)
                    }
                }
//                else -> GoalCategoryFragment().apply {
//                    arguments = (arguments ?: Bundle()).apply {
//                        putString("goalOwnerName", goalOwnerName)
//                    }
                else -> GoalSelectFragment()
            }


            // GoalSelectFragment를 GoalCategoryFragment로 가정
//            val first = GoalCategoryFragment().apply {
//                arguments = (arguments ?: Bundle()).apply {
//                    putString("goalOwnerName", goalOwnerName)
//                }
//            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.goal_container, startFragment)
                .commit()
//        }
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


    private fun loadLastGoalData() {
        val sharedPref = getSharedPreferences("user_data", Context.MODE_PRIVATE)

        goalName = sharedPref.getString("last_goal_name", goalName) ?: goalName
        goalAmount = sharedPref.getString("last_goal_amount", goalAmount) ?: goalAmount
        goalCategory = sharedPref.getString("last_goal_category", goalCategory) ?: goalCategory
        goalType = sharedPref.getString("last_goal_type", goalType) ?: goalType
        oneDose = sharedPref.getString("last_one_dose", oneDose) ?: oneDose
        frequency = sharedPref.getInt("last_frequency", frequency)
        period = sharedPref.getString("last_period", period) ?: period
        endDate = sharedPref.getString("last_end_date", endDate) ?: endDate
        verificationType =
            sharedPref.getString("last_verification_type", verificationType) ?: verificationType
        limitFriendCount = sharedPref.getInt("last_limit_friend_count", limitFriendCount)
        goalTime = sharedPref.getInt("last_goal_time", goalTime)

        // 알림 설정 데이터도 불러오기
        notificationEnabled =
            sharedPref.getBoolean("last_notification_enabled", notificationEnabled)
        regularAlertEnabled =
            sharedPref.getBoolean("last_regular_alert_enabled", regularAlertEnabled)
        alertTimeOfDay =
            sharedPref.getString("last_alert_time_of_day", alertTimeOfDay) ?: alertTimeOfDay
        alertHour = sharedPref.getString("last_alert_hour", alertHour) ?: alertHour
        alertMinute = sharedPref.getString("last_alert_minute", alertMinute) ?: alertMinute
        alertDays =
            sharedPref.getStringSet("last_alert_days", alertDays)?.toMutableSet() ?: alertDays
    }

    fun saveGoalData() {
        val sharedPref = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        editor.putString("last_goal_name", goalName)
        editor.putString("last_goal_amount", goalAmount)
        editor.putString("last_goal_category", goalCategory)
        editor.putString("last_goal_type", goalType)
        editor.putString("last_one_dose", oneDose)
        editor.putInt("last_frequency", frequency)
        editor.putString("last_period", period)
        editor.putString("last_end_date", endDate)
        editor.putString("last_verification_type", verificationType)
        editor.putInt("last_limit_friend_count", limitFriendCount)
        editor.putInt("last_goal_time", goalTime)

        // 알림 설정 데이터 저장
        editor.putBoolean("last_notification_enabled", notificationEnabled)
        editor.putBoolean("last_regular_alert_enabled", regularAlertEnabled)
        editor.putString("last_alert_time_of_day", alertTimeOfDay)
        editor.putString("last_alert_hour", alertHour)
        editor.putString("last_alert_minute", alertMinute)
        editor.putStringSet("last_alert_days", alertDays)

        editor.apply()
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
