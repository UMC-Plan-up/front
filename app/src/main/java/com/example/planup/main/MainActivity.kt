package com.example.planup.main

import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.databinding.ActivityMainBinding
import com.example.planup.main.friend.ui.FriendFragment
import com.example.planup.main.goal.ui.GoalFragment
import com.example.planup.main.goal.ui.SubscriptionPlanFragment
import com.example.planup.main.home.ui.HomeFragment
import com.example.planup.main.my.ui.MypageEmailLinkFragment
import com.example.planup.main.my.ui.MypageFragment
import com.example.planup.main.my.ui.MypagePasswordChangeFragment
import com.example.planup.main.my.ui.MypagePasswordLinkFragment
import com.example.planup.main.record.ui.RecordFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var prefs: SharedPreferences
    private lateinit var editor: Editor



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = getSharedPreferences("userInfo", MODE_PRIVATE)
        editor = prefs.edit()

        val actionNavigate = intent.getStringExtra("ACTION_NAVIGATE")
        val isFromGoalDetail = intent.getBooleanExtra("IS_FROM_GOAL_DETAIL", false)

        // onCreate에서 시작할 화면 결정
        val startFragment = when {
            isFromGoalDetail -> {
                SubscriptionPlanFragment().apply {
                    arguments = Bundle().apply {
                        putBoolean("IS_FROM_GOAL_DETAIL", true)
                    }
                }
            }
            // 딥링크 로직
            else -> HomeFragment()
        }
        initBottomNavigation(startFragment)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val deeplinkFragment = when {
            intent?.action == Intent.ACTION_VIEW -> {
                val data: Uri? = intent?.data
                Log.d("okhttp", "sceme: ${data?.scheme} host: ${data?.host} path: ${data?.path}")
                when {
                    data?.host.equals("mypage") && data?.path?.startsWith("/password")!! && data.getQueryParameter(
                        "verified"
                    ).equals("true") -> {
                        MypagePasswordChangeFragment().apply {
                            arguments = Bundle().apply {
                                putString("verificationToken", prefs.getString("verificationToken","no-data"))
                                editor.remove("passwordToken")
                                editor.apply()
                            }
                        }
                    }

                    data?.host.equals("email") && data?.path?.startsWith("/change")!! && data.getQueryParameter(
                        "verified"
                    ).equals("true") -> {
                        MypageEmailLinkFragment().apply {
                            arguments = Bundle().apply {
                                putBoolean("deepLink", true)
                            }
                        }
                    }

                    else -> HomeFragment()
                }
            } else -> HomeFragment()
        }

        initBottomNavigation(deeplinkFragment)
    }

    fun navigateToFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, fragment)
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }

    private fun initBottomNavigation(fragment: Fragment) {
        binding.bottomNavigationView.selectedItemId = R.id.fragment_home
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, fragment)
            .commitAllowingStateLoss()

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
}