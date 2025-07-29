package com.example.planup.goal.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.databinding.FragmentChallengeTimerPhotoBinding
import com.example.planup.goal.GoalActivity

class ChallengeTimerPhotoFragment:Fragment() {
    lateinit var binding: FragmentChallengeTimerPhotoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChallengeTimerPhotoBinding.inflate(inflater,container,false)
        clickListener()
        return binding.root
    }

    private fun clickListener(){

        //뒤로가기 목표명, 1회 분량 설정
        binding.backIv.setOnClickListener {
            (context as GoalActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.goal_container,ChallengeSetGoalFragment())
                .commitAllowingStateLoss()
        }

        //타이머 인증
        binding.timerCl.setOnClickListener {
            binding.timerCl.isSelected = true
            binding.photoCl.isSelected = false
        }
        //사진 인증
        binding.photoCl.setOnClickListener {
            binding.timerCl.isSelected = false
            binding.photoCl.isSelected = true
        }
        //타이머 설정 또는 사진 설정 페이지로 이동
        binding.btnNextTv.setOnClickListener{
            if (!binding.timerCl.isSelected && !binding.photoCl.isSelected) return@setOnClickListener
            if (binding.timerCl.isSelected) {
                (context as GoalActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.goal_container,ChallengeSetTimerFragment())
                    .commitAllowingStateLoss()
            } else if (binding.photoCl.isSelected){
                (context as GoalActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.goal_container,ChallengeSetPhotoFragment())
                    .commitAllowingStateLoss()
            }

        }
    }
}