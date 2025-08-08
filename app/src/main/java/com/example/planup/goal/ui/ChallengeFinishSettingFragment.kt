package com.example.planup.goal.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.planup.databinding.FragmentChallengeFinishSettingBinding
import com.example.planup.goal.GoalActivity
import com.example.planup.main.MainActivity

//친구에게 받은 챌린지 요청에 대한 페널티 재설정이 완료되었을 때 페이지
class ChallengeFinishSettingFragment:Fragment() {
    lateinit var binding: FragmentChallengeFinishSettingBinding
    lateinit var friendName: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChallengeFinishSettingBinding.inflate(inflater,container,false)
        init() //프레그먼트 초기 셋팅
        clickListener() //클릭 이벤트
        return binding.root
    }

    private fun init(){
        //나에게 챌린지를 보낸 친구의 이름
        friendName = arguments?.getString("friend","friend").toString()
    }
    private fun clickListener(){
        //"네, 확인했어요" 버튼 클릭 시 홈 화면으로 이동
        binding.challengeCheckedBtn.setOnClickListener {
            val intent = Intent(context as GoalActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }
}