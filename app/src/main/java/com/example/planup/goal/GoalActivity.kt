package com.example.planup.goal
//회원가입 후 목표설정 플로우
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.example.planup.R
import com.example.planup.databinding.ActivityGoalBinding
import com.example.planup.goal.ui.GoalSelectFragment

class GoalActivity:AppCompatActivity() {

    lateinit var binding: ActivityGoalBinding

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        binding = ActivityGoalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction() //함께 목표 설정 또는 챌린지 설정 선택하는 프레그먼트를 기본 프레그먼트로 사용
            .replace(R.id.goal_container,GoalSelectFragment())
            .commitAllowingStateLoss()
    }

}