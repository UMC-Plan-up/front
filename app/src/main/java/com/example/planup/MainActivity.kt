package com.example.planup

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.planup.databinding.ActivityMainBinding
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.planup.friend.FriendFragment
import com.example.planup.mypage.MypageFragment
import com.example.planup.record.RecordFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initBottomNavigation()
    }

    private fun initBottomNavigation() {

        binding.bottomNavigationView.selectedItemId = R.id.fragment_home
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, HomeFragment())
            .commitAllowingStateLoss()

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.fragment_goal -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_container,GoalFragment())
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
                        .replace(R.id.main_container,MypageFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }
}