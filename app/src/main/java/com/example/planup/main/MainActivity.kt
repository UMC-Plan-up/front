package com.example.planup.main

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.planup.R
import com.example.planup.component.snackbar.BlueSnackbarHost
import com.example.planup.component.snackbar.GraySnackbarHost
import com.example.planup.databinding.ActivityMainBinding
import com.example.planup.main.friend.ui.FriendFragment
import com.example.planup.main.friend.ui.viewmodel.FriendUiMessage
import com.example.planup.main.friend.ui.viewmodel.FriendViewModel
import com.example.planup.main.goal.ui.GoalFragment
import com.example.planup.main.goal.ui.SubscriptionPlanFragment
import com.example.planup.main.home.ui.HomeFragment
import com.example.planup.main.my.ui.MypageFragment
import com.example.planup.main.my.ui.dialog.EmailChangeSuccessDialog
import com.example.planup.main.my.ui.viewmodel.MyPageEmailChangeViewModel
import com.example.planup.main.record.ui.RecordFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var prefs: SharedPreferences
    private lateinit var editor: Editor


    private val mainSnackbarViewModel : MainSnackbarViewModel by viewModels()
    private val friendViewModel : FriendViewModel by viewModels()
    private val myPageEmailChangeViewModel : MyPageEmailChangeViewModel by viewModels()

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.enableEdgeToEdge(window)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                insets.left,
                insets.top,
                insets.right,
                insets.bottom
            )

            WindowInsetsCompat.CONSUMED
        }

        prefs = getSharedPreferences("userInfo", MODE_PRIVATE)
        editor = prefs.edit()

        val fromChallenge = intent.getStringExtra("FROM_CHALLENGE_TO")
        val isFromGoalDetail = intent.getBooleanExtra("IS_FROM_GOAL_DETAIL", false)

        binding.composeSnackbar.setContent {
            val errorSnackBarHost = remember { SnackbarHostState() }
            val blueSnackBarHost = remember { SnackbarHostState() }
            LaunchedEffect(Unit) {
                mainSnackbarViewModel.snackbarErrorEvents.collect { event ->
                    errorSnackBarHost.showSnackbar(event.message)
                }
            }
            LaunchedEffect(Unit) {
                mainSnackbarViewModel.snackbarBlueEvents.collect { event ->
                    blueSnackBarHost.showSnackbar(event.message)
                }
            }
            GraySnackbarHost(
                hostState = errorSnackBarHost
            )
            BlueSnackbarHost(
                hostState = blueSnackBarHost
            )
        }

        val startFragment = if (isFromGoalDetail){
            SubscriptionPlanFragment().apply {
                    arguments = Bundle().apply {
                        putBoolean("IS_FROM_GOAL_DETAIL", true)
                    }
                }
        } else if (fromChallenge == "GoalFragment") {
            GoalFragment()
        } else if(fromChallenge == "RecordFragment") {
            RecordFragment()
        } else {
            HomeFragment()
        }
        // onCreate에서 시작할 화면 결정
//        val startFragment = when {
//            isFromGoalDetail -> {
//                SubscriptionPlanFragment().apply {
//                    arguments = Bundle().apply {
//                        putBoolean("IS_FROM_GOAL_DETAIL", true)
//                    }
//                }
//            }
//            else -> HomeFragment()
//        }
        initBottomNavigation(startFragment)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                launch {
                    friendViewModel.uiMessage.collect { uiMessage ->
                        when (uiMessage) {
                            is FriendUiMessage.Error -> {
                                mainSnackbarViewModel.updateErrorMessage(uiMessage.msg)
                            }

                            is FriendUiMessage.ReportSuccess -> {
                                mainSnackbarViewModel.updateSuccessMessage(
                                    getString(R.string.toast_report)
                                )
                            }

                            is FriendUiMessage.BlockSuccess -> {
                                mainSnackbarViewModel.updateSuccessMessage(
                                    getString(R.string.toast_block, uiMessage.friendName)
                                )
                            }

                            is FriendUiMessage.DeleteSuccess -> {
                                mainSnackbarViewModel.updateSuccessMessage(
                                    getString(R.string.toast_delete, uiMessage.friendName)
                                )
                            }

                            is FriendUiMessage.AcceptSuccess -> {
                                mainSnackbarViewModel.updateSuccessMessage(
                                    getString(R.string.toast_accept_friend, uiMessage.friendName)
                                )
                            }

                            is FriendUiMessage.DeclineSuccess -> {
                                mainSnackbarViewModel.updateSuccessMessage(
                                    getString(R.string.toast_decline_friend, uiMessage.friendName)
                                )
                            }
                        }
                    }
                }
                launch {
                    myPageEmailChangeViewModel.showAlertEmail.collect { email ->
                        Log.d("JWH","Show : $email")
                        email?.let { email ->
                            val dialog = EmailChangeSuccessDialog.getInstance(email)
                            dialog.show(supportFragmentManager, "EmailChangeSuccessDialog")
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        when(intent.action) {
            Intent.ACTION_VIEW -> {
                val data = intent.data
                Log.d("JWH",data.toString())
                data ?: return
                val host = data.host
                when(host) {
                    "mypage" -> {

                    }
                    "email" -> {
                        if (data.path?.startsWith("/change") == true && data.getQueryParameter("verified") == "true") {
                            //이메일 변경 완료 됨 검증
                            val token = data.getQueryParameter("token")
                            myPageEmailChangeViewModel.verifyScheme(token)
                        }
                    }
                }
            }
        }
//        val deeplinkFragment = when {
//            intent?.action == Intent.ACTION_VIEW -> {
//                val data: Uri? = intent?.data
//                Log.d("okhttp", "sceme: ${data?.scheme} host: ${data?.host} path: ${data?.path}")
//                when {
//                    data?.host.equals("mypage") && data?.path?.startsWith("/password")!! && data.getQueryParameter(
//                        "verified"
//                    ).equals("true") -> {
//                        MypagePasswordChangeFragment().apply {
//                            arguments = Bundle().apply {
//                                putString("verificationToken", prefs.getString("verificationToken","no-data"))
//                                editor.remove("passwordToken")
//                                editor.apply()
//                            }
//                        }
//                    }
//
//                    data?.host.equals("email") && data?.path?.startsWith("/change")!! && data.getQueryParameter(
//                        "verified"
//                    ).equals("true") -> {
//                        MypageEmailLinkFragment().apply {
//                            arguments = Bundle().apply {
//                                putBoolean("deepLink", true)
//                            }
//                        }
//                    }
//
//                    else -> HomeFragment()
//                }
//            } else -> HomeFragment()
//        }

    }

    fun navigateToFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, fragment)
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }

    private fun initBottomNavigation(fragment: Fragment) {
        binding.bottomNavigationView.selectedItemId = R.id.fragment_home
        navigateToFragment(fragment)
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.fragment_goal -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_container, GoalFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.fragment_record -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_container, RecordFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.fragment_home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_container, HomeFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.fragment_friend -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_container, FriendFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.fragment_mypage -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_container, MypageFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }

    companion object {
        fun getIntent(context: Context): Intent = Intent(context, MainActivity::class.java)
            .apply {
                addFlags(
                    Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                )
            }
    }
}