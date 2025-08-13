package com.example.planup.goal.ui

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.databinding.FragmentChallengeReceivedBinding
import com.example.planup.goal.GoalActivity
import com.example.planup.goal.adapter.AcceptChallengeAdapter
import com.example.planup.goal.adapter.RejectChallengeAdapter
import com.example.planup.main.MainActivity
import com.example.planup.network.controller.ChallengeController

class ChallengeReceivedFragment:Fragment(), RejectChallengeAdapter, AcceptChallengeAdapter {
    private lateinit var binding: FragmentChallengeReceivedBinding

    //챌린지 정보 저장
    private lateinit var prefs: SharedPreferences
    private lateinit var editor: Editor
    private lateinit var friendName: String

    //API 연동을 위한 서비스
    private lateinit var service: ChallengeController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChallengeReceivedBinding.inflate(inflater,container,false)
        init()
        clickListener()
        return binding.root
    }
    //프레그먼트 초기화
    private fun init(){
        prefs = (context as GoalActivity).getSharedPreferences("challenge",MODE_PRIVATE)
        editor = prefs.edit()
        //친구 이름
        binding.challengeRequestTitleTv.text = getString(R.string.challenge_request_from,arguments?.getString("friendName","null"))
        friendName = getString(R.string.challenge_request_from,arguments?.getString("friendName","null"))
        //목표명
        binding.challengeRequestGoalEnterTv.text = arguments?.getString("goalName","목표명이 없습니다")
        //1회 분량
        binding.challengeRequestAmountEnterTv.text = arguments?.getString("goalAmount","1회 분량이 없습니다")
        //인증 방식에 따라 타이머, 사진인증에 맞게 인증방식 출력
        if (arguments?.getString("Type","null").equals("timer")){
            val totalTime = arguments?.getInt("targetTime",0)
            val hour = totalTime?.div(3600)
            val minute = (totalTime?.minus(hour?.times(3600) ?: 0))?.div(60)
            val second = totalTime?.minus(((hour?.times(3600) ?: 0) - (minute?.times(60) ?: 0)))
            binding.challengeRequestVerifyEnterIv.setImageResource(R.drawable.ic_timer)
            binding.challengeRequestVerifyEnterTv.setText(R.string.auth_timer)
            binding.challengeRequestHourEnterTv.text = hour?.toString()
            binding.challengeRequestMinuteEnterTv.text = minute?.toString()
            binding.challengeRequestSecondEnterTv.text = second?.toString()
        } else if (arguments?.getString("Type","null").equals("photo")){
            binding.challengeRequestVerifyEnterIv.setImageResource(R.drawable.ic_picture)
            binding.challengeRequestVerifyEnterTv.setText(R.string.auth_photo)
        }
        //종료일
        binding.challengeRequestDueEnterTv.text = arguments?.getString("endDay",(getString(R.string.end_day,0,0," ")))
        //기준 기간
        binding.challengeRequestDurationEnterTv.text = arguments?.getString("duration","0")
        //빈도
        binding.challengeRequestFrequencyEnterTv.text = arguments?.getString("frequency","0")
        //페널티
        if (arguments?.getString("penalty","null").equals("coffee")){
            binding.challengeRequestPenaltyEnterIv.setImageResource(R.drawable.ic_coffee)
            binding.challengeRequestPenaltyEnterTv.setText(R.string.challenge_penalty_coffee)
        } else if (arguments?.getString("penalty","null").equals("snack")){
            binding.challengeRequestPenaltyEnterIv.setImageResource(R.drawable.ic_burrito)
            binding.challengeRequestPenaltyEnterTv.setText(R.string.challenge_penalty_snack)
        } else if (arguments?.getString("penalty","null").equals("cleaning")){
            binding.challengeRequestPenaltyEnterIv.setImageResource(R.drawable.ic_bucket)
            binding.challengeRequestPenaltyEnterTv.setText(R.string.challenge_penalty_cleaning)
        } else if (arguments?.getString("penalty","null").equals("wish")){
            binding.challengeRequestPenaltyEnterIv.setImageResource(R.drawable.ic_megaphone)
            binding.challengeRequestPenaltyEnterTv.setText(R.string.challenge_penalty_wish)
        } else if (arguments?.getString("penalty","null").equals("present")){
            binding.challengeRequestPenaltyEnterIv.setImageResource(R.drawable.ic_money)
            binding.challengeRequestPenaltyEnterTv.setText(R.string.challenge_penalty_present)
        } else if (arguments?.getString("penalty","null").equals("skip")){
            binding.challengeRequestPenaltyEnterIv.setImageResource(R.drawable.img_none_friend)
            binding.challengeRequestPenaltyEnterTv.setText(R.string.challenge_penalty_skip)
        } else {
            binding.challengeRequestPenaltyEnterIv.setImageResource(R.drawable.ic_pencil)
            binding.challengeRequestPenaltyEnterTv.text = arguments?.getString("penalty","null")
        }
    }
    //클릭 이벤트 처리
    private fun clickListener(){
        binding.btnRejectTv.setOnClickListener {
            val service = ChallengeController()
            service.setRejectChallengeAdapter(this)
        }
        binding.btnAcceptTv.setOnClickListener {
            val service = ChallengeController()
            service.setAcceptChallengeAdapter(this)
        }
        //새로운 페널티 제안하기에서 뒤로 왔을 때 기존 데이터를 보여줄 수 있음
        binding.btnAskNewPenaltyTv.setOnClickListener {
            val askNewPenaltyFragment = AskNewPenaltyFragment()
            askNewPenaltyFragment.arguments = Bundle().apply {
                putString("friendName",friendName)
            }
            (context as GoalActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.goal_container,askNewPenaltyFragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }
    }
    //API 연동 오류 시 토스트 메시지 출력
    private fun errorToast(message: String){
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.toast_grey_template,null)
        layout.findViewById<TextView>(R.id.toast_grey_template_tv).text = message

        val toast = Toast(context)
        toast.view = layout
        toast.duration = LENGTH_SHORT
        toast.setGravity(Gravity.BOTTOM,0,300)
        toast.show()
    }
    //수락 또는 거절 토스트 메시지 출력
    private fun makeToast(message: Int){
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.toast_grey_template,null)
        layout.findViewById<TextView>(R.id.toast_grey_template_tv).setText(message)

        val toast = Toast(context)
        toast.view = layout
        toast.duration = LENGTH_SHORT
        toast.setGravity(Gravity.BOTTOM,0,300)
        toast.show()
    }
    //수락 또는 거절 클릭 시 홈 화면으로 이동
    private fun goToHome(){
        val intent = Intent(context as GoalActivity, MainActivity::class.java)
        startActivity(intent)
    }

    //거절하기 API 성공
    override fun successReject() {
        makeToast(R.string.toast_challenge_reject)
        goToHome()
    }
    //거절하기 API 오류
    override fun failReject(message: String) {
        errorToast(message)
    }

    //수락하기 API 성공
    override fun successAccept() {
        makeToast(R.string.toast_challenge_accept)
        goToHome()
    }
    //수락하기 API 오류
    override fun failAccept(message: String) {
        errorToast(message)
    }
}