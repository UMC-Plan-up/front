package com.example.planup.main

import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.databinding.ActivityMainBinding
import com.example.planup.main.friend.ui.FriendFragment
import com.example.planup.main.goal.ui.GoalFragment
import com.example.planup.main.home.adapter.UserInfoAdapter
import com.example.planup.main.home.ui.HomeFragment
import com.example.planup.main.my.ui.MypageEmailLinkFragment
import com.example.planup.main.my.ui.MypageFragment
import com.example.planup.main.my.ui.MypagePasswordChangeFragment
import com.example.planup.main.my.ui.MypagePasswordLinkFragment
import com.example.planup.main.record.ui.RecordFragment
import com.example.planup.network.controller.UserController
import com.example.planup.network.data.UserInfo

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val action: String? = intent?.action
        var data: Uri? = intent?.data

        //딥링크의 data가 intent-filter와 일치하는 경우 해당 프레그먼트로 이동
        if (data?.scheme.equals("planup")
            && data?.host.equals("password")
            && data?.path?.startsWith("/change")!!
            && data != null
        ) {
            if (action == Intent.ACTION_VIEW
                && data.getQueryParameter("verified").equals("true")
            ) {
                initBottomNavigation(MypagePasswordChangeFragment())
            }
        } else if (data?.scheme.equals("planup")
            && data?.host.equals("email")
            && data?.path?.startsWith("/change")!!
            && data != null
        ) {
            if (action == Intent.ACTION_VIEW
                && data.getQueryParameter("verified").equals("true")
            ) {
                val emailLinkFragment = MypageEmailLinkFragment()
                emailLinkFragment.arguments = Bundle().apply {
                    putBoolean("deepLink",true)
                }
                initBottomNavigation(emailLinkFragment)
            }
        } else {
            //딥링크의 data가 없거나 일치하는 intent-filter가 없는 경우 기존 방식대로 운영
            initBottomNavigation(HomeFragment())
        }
    }

    fun navigateFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, fragment)
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
