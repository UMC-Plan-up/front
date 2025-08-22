package com.example.planup.goal

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.planup.R
import com.example.planup.databinding.ActivityGoalBinding
import com.example.planup.goal.data.GoalViewModel
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


    /* 화면 터치 시 EditText 밖을 누르면 키보드 숨기기 */
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            currentFocus?.let { view ->
                if (view is EditText) { // 현재 포커스가 EditText일 경우만
                    val outRect = android.graphics.Rect()
                    view.getGlobalVisibleRect(outRect)
                    if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                        view.clearFocus()
                        hideKeyboard(view) // 키보드 숨김
                    }
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    // 혹시 dispatchTouchEvent에서 놓치는 경우 보완
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            currentFocus?.let { view ->
                if (view is EditText) {
                    view.clearFocus()
                    hideKeyboard(view)
                }
            }
        }
        return super.onTouchEvent(event)
    }

    //화면 터치 시 키보드 사라지게
    private fun hideKeyboard(view: View?) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        view?.let {
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    private val subscriptionResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val isUnlocked = data?.getBooleanExtra("IS_UNLOCKED", false) ?: false
            if (isUnlocked) {
                val goalDetailFragment =
                    supportFragmentManager.findFragmentById(R.id.goal_container)
                if (goalDetailFragment is GoalDetailFragment) {
                    goalDetailFragment.updateLockStatus(true)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val goalViewModel = ViewModelProvider(this).get(GoalViewModel::class.java)
        goalViewModel.fromWhere.value = intent.getStringExtra("TO_CHALLENGE_FROM")
        Log.d("okhttpasdfdsfdassssss", goalViewModel.fromWhere.value.toString())
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

                else -> GoalSelectFragment().apply {
//                        putString("from",intent.getStringExtra("TO_CHALLENGE_FROM"))
                }
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.goal_container, startFragment)
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

    /* SubscriptionPlanFragment를 시작하고 결과를 받기 위한 함수 */
    fun startSubscriptionActivity() {
        val intent = Intent(this, MainActivity::class.java)
        // 어떤 경로로 SubscriptionFragment가 호출되었는지 판단
        intent.putExtra("ACTION_NAVIGATE", "SHOW_SUBSCRIPTION")
        intent.putExtra("IS_FROM_GOAL_DETAIL", true)
        subscriptionResultLauncher.launch(intent)
    }

    fun draftPrefs() = getSharedPreferences("user_data", MODE_PRIVATE)

    fun saveDraft(map: Map<String, Any?>) {
        draftPrefs().edit().apply {
            map.forEach { (k, v) ->
                when (v) {
                    is Int    -> putInt(k, v)
                    is Boolean-> putBoolean(k, v)
                    else      -> Unit
                }
            }
        }.apply()
    }

    fun readDraftString(key: String, def: String = "") =
        draftPrefs().getString(key, def) ?: def
 
    fun readDraftInt(key: String, def: Int = 0) =
        draftPrefs().getInt(key, def)
    fun readDraftBool(key: String, def: Boolean = false) =
        draftPrefs().getBoolean(key, def)
}
