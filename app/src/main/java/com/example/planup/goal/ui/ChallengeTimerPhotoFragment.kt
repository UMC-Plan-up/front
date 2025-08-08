package com.example.planup.goal.ui

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.databinding.FragmentChallengeTimerPhotoBinding
import com.example.planup.goal.GoalActivity

class ChallengeTimerPhotoFragment:Fragment() {
    lateinit var binding: FragmentChallengeTimerPhotoBinding

    lateinit var prefs: SharedPreferences //챌린지 정보 저장을 위한 sharedPreferences
    lateinit var editor: Editor //sharedPreferences editor

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChallengeTimerPhotoBinding.inflate(inflater,container,false)
        init()
        clickListener()
        return binding.root
    }

    //프레그먼트 초기화
    private fun init(){
        //챌린지 정보를 저장할 sharedPreferences 생성
        prefs = (context as GoalActivity).getSharedPreferences("challenge", MODE_PRIVATE)
        editor = prefs.edit()
    }

    private fun clickListener(){

        //뒤로가기 목표명, 1회 분량 설정
        binding.backIv.setOnClickListener {
            (context as GoalActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.goal_container,ChallengeSetGoalFragment())
                .commitAllowingStateLoss()
        }

        //타이머 인증 선택
        binding.timerCl.setOnClickListener {
            binding.timerCl.isSelected = true
            binding.photoCl.isSelected = false
        }
        //사진 인증 선택
        binding.photoCl.setOnClickListener {
            binding.timerCl.isSelected = false
            binding.photoCl.isSelected = true
        }
        //타이머 설정 또는 사진 설정 페이지로 이동
        binding.btnNextTv.setOnClickListener{
            if (!binding.timerCl.isSelected && !binding.photoCl.isSelected) return@setOnClickListener
            if (binding.timerCl.isSelected) { //타이머 인증을 선택한 경우
                editor.putString("goalType","CHALLENGE_TIME")
                (context as GoalActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.goal_container,ChallengeSetTimerFragment())
                    .commitAllowingStateLoss()
            } else if (binding.photoCl.isSelected){ //사진 인증을 선택한 경우
                editor.putString("goalType","CHALLENGE_PHOTO")
                val frequencyFragment = ChallengeSetFrequencyFragment()
                frequencyFragment.arguments = Bundle().apply {
                    putString("previous","photo")
                }
                (context as GoalActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.goal_container,frequencyFragment)
                    .commitAllowingStateLoss()
            }

        }
    }
}