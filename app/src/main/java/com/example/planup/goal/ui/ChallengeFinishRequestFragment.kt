package com.example.planup.goal.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.databinding.FragmentChallengeFinishRequestBinding
import com.example.planup.databinding.FragmentChallengeFriendBinding
import com.example.planup.goal.GoalActivity
import com.example.planup.main.MainActivity

class ChallengeFinishRequestFragment:Fragment() {
    lateinit var binding: FragmentChallengeFinishRequestBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChallengeFinishRequestBinding.inflate(inflater,container,false)
        clickListener()
        return binding.root
    }
    private fun clickListener(){
        //뒤로가기: 친구에 챌린지 신청 프레그먼트로 이동
        binding.challengeFinishRequestBackIv.setOnClickListener {
            (context as GoalActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.goal_container,ChallengeFriendFragment())
                .commitAllowingStateLoss()
        }
        //완료 버튼: 메인 액티비티, 홈 프레그먼트로 이동
        binding.challengeFinishCompleteBtn.setOnClickListener {
            (context as GoalActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.goal_container,ChallengeSetAlertFragment())
                .commitAllowingStateLoss()
//            val intent = Intent(context as GoalActivity,MainActivity::class.java)
//            startActivity(intent)
        }
    }
}