package com.example.planup.goal.ui

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.databinding.FragmentGoalSelectBinding
import com.example.planup.main.MainActivity

class GoalSelectFragment : Fragment() {
    lateinit var binding: FragmentGoalSelectBinding
    var sleepChallenge: Int = 2 //비활성화된 챌린지 목표 개수

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGoalSelectBinding.inflate(inflater, container, false)
        clickListener()
        return binding.root
    }

    private fun clickListener() {
        //함께 목표 설정하기 클릭
        binding.goalSettingTogetherCl.setOnClickListener {
            binding.goalSettingTogetherCl.isSelected = true
            binding.goalSettingChallengeCl.isSelected = false
        }

        //1:1 챌린지 도전하기 클릭
        binding.goalSettingChallengeCl.setOnClickListener {
            binding.goalSettingTogetherCl.isSelected = false
            binding.goalSettingChallengeCl.isSelected = true
        }

        //다음 버튼 클릭 후 선택한 목표에 따른 페이지 이동
        binding.goalSelectNextBtn.setOnClickListener {
            if (binding.goalSettingTogetherCl.isSelected) {
                (context as MainActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.goal_container, ChallengeSettingFragment())
                    .commitAllowingStateLoss()
            } else if (binding.goalSettingChallengeCl.isSelected) {
                if(sleepChallenge==2){
                    makeToast()
                    sleepChallenge --
                    return@setOnClickListener
                }
                (context as MainActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.goal_container, ChallengeSettingFragment())
                    .commitAllowingStateLoss()
            }
        }
    }
    private fun makeToast(){
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.toast_grey_template,null)
        layout.findViewById<TextView>(R.id.toast_grey_template_tv).text = getString(R.string.toast_challenge_setting)

        val toast = Toast(context)
        toast.view = layout
        toast.duration = LENGTH_SHORT
        toast.setGravity(Gravity.CENTER_HORIZONTAL,0,300)
        toast.show()
    }
}