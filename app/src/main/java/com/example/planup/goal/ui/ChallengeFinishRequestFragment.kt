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

//1:1 챌린지 신청 완료되었을 때 페이지
class ChallengeFinishRequestFragment:Fragment() {
    lateinit var binding: FragmentChallengeFinishRequestBinding
    lateinit var friend: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChallengeFinishRequestBinding.inflate(inflater,container,false)
        init()
        clickListener()
        return binding.root
    }
    private fun init(){
        friend = arguments?.getString("friend","친구").toString()
        binding.challengeFinishRequestSubtitleTv.text = getString(R.string.challenge_finish_request_subtitle,friend)
    }
    private fun clickListener(){
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