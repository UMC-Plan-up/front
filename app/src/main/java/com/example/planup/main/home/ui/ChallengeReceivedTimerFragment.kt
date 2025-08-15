package com.example.planup.main.home.ui

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
import com.example.planup.databinding.FragmentChallengeReceivedTimerBinding
import com.example.planup.goal.GoalActivity
import com.example.planup.goal.adapter.AcceptChallengeAdapter
import com.example.planup.goal.adapter.RejectChallengeAdapter
import com.example.planup.main.home.data.ChallengeReceivedTimer
import com.example.planup.main.MainActivity
import com.example.planup.network.controller.ChallengeController

class ChallengeReceivedTimerFragment : Fragment(), RejectChallengeAdapter, AcceptChallengeAdapter {
    private lateinit var binding: FragmentChallengeReceivedTimerBinding

    // 챌린지 정보 저장
    private lateinit var challengeInfo: ChallengeReceivedTimer

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChallengeReceivedTimerBinding.inflate(inflater, container, false)
        init()
        clickListener()
        return binding.root
    }

    // 프래그먼트 초기화
    private fun init() {
        // 챌린지 정보 사용하기
        challengeInfo = requireArguments().getParcelable("receivedChallenge")!!

        // 친구 이름
        binding.challengeTimerTitleTv.text =
            getString(R.string.challenge_request_from, challengeInfo.friendName)

        // 목표명
        binding.challengeTimerGoalEnterTv.text = challengeInfo.goalName

        // 1회 분량
        binding.challengeTimerAmountEnterTv.text = challengeInfo.goalAmount

        // 인증방식
        binding.challengeTimerVerifyEnterIv.setImageResource(R.drawable.ic_timer)
        binding.challengeTimerVerifyEnterTv.setText(R.string.auth_timer)

        // 타이머 정보
        val totalTime = challengeInfo.targetTime
        val hour = totalTime / 3600
        val minute = (totalTime - hour * 3600) / 60
        val second = totalTime - hour * 3600 - minute * 60
        binding.challengeTimerHourEnterTv.text = hour.toString()
        binding.challengeTimerMinuteEnterTv.text = minute.toString()
        binding.challengeTimerSecondEnterTv.text = second.toString()

        // 종료일
        binding.challengeTimerDueEnterTv.text = challengeInfo.endDate

        // 기준 기간
        binding.challengeTimerDurationEnterTv.text = challengeInfo.duration

        // 빈도
        binding.challengeTimerFrequencyEnterTv.text = challengeInfo.frequency

        // 페널티
        when (challengeInfo.penalty) {
            "coffee" -> {
                binding.challengeTimerPenaltyEnterIv.setImageResource(R.drawable.ic_coffee)
                binding.challengeTimerPenaltyEnterTv.setText(R.string.challenge_penalty_coffee)
            }

            "snack" -> {
                binding.challengeTimerPenaltyEnterIv.setImageResource(R.drawable.ic_burrito)
                binding.challengeTimerPenaltyEnterTv.setText(R.string.challenge_penalty_snack)
            }

            "cleaning" -> {
                binding.challengeTimerPenaltyEnterIv.setImageResource(R.drawable.ic_bucket)
                binding.challengeTimerPenaltyEnterTv.setText(R.string.challenge_penalty_cleaning)
            }

            "wish" -> {
                binding.challengeTimerPenaltyEnterIv.setImageResource(R.drawable.ic_megaphone)
                binding.challengeTimerPenaltyEnterTv.setText(R.string.challenge_penalty_wish)
            }

            "present" -> {
                binding.challengeTimerPenaltyEnterIv.setImageResource(R.drawable.ic_money)
                binding.challengeTimerPenaltyEnterTv.setText(R.string.challenge_penalty_present)
            }

            "skip" -> {
                binding.challengeTimerPenaltyEnterIv.setImageResource(R.drawable.img_none_friend)
                binding.challengeTimerPenaltyEnterTv.setText(R.string.challenge_penalty_skip)
            }

            else -> {
                binding.challengeTimerPenaltyEnterIv.setImageResource(R.drawable.ic_pencil)
                binding.challengeTimerPenaltyEnterTv.text = challengeInfo.penalty
            }
        }
    }

    // 클릭 이벤트 처리
    private fun clickListener() {
        binding.btnRejectTv.setOnClickListener {
            val service = ChallengeController()
            service.setRejectChallengeAdapter(this)
            service.rejectChallenge(challengeInfo.userId, challengeInfo.challengeId)
        }
        binding.btnAcceptTv.setOnClickListener {
            val service = ChallengeController()
            service.setAcceptChallengeAdapter(this)
            service.acceptChallenge(challengeInfo.challengeId, challengeInfo.userId)
        }
        // 새로운 페널티 제안하기 페이지로 이동
        binding.btnAskNewPenaltyTv.setOnClickListener {
            val askNewPenaltyFragment = AskNewPenaltyFragment().apply {
                arguments = Bundle().apply {
                    putInt("userId", challengeInfo.userId)
                    putInt("challengeId", challengeInfo.challengeId)
                    putSerializable("friendIdList", ArrayList(challengeInfo.friendId))
                    putString("friendName", challengeInfo.friendName)
                }
            }
            (requireActivity() as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, askNewPenaltyFragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }
    }

    // API 연동 오류 시 토스트 메시지 출력
    private fun errorToast(message: String) {
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.toast_grey_template, null)
        layout.findViewById<TextView>(R.id.toast_grey_template_tv).text = message

        val toast = Toast(context)
        toast.view = layout
        toast.duration = LENGTH_SHORT
        toast.setGravity(Gravity.BOTTOM, 0, 300)
        toast.show()
    }

    // 수락 또는 거절 토스트 메시지 출력
    private fun makeToast(message: Int) {
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.toast_grey_template, null)
        layout.findViewById<TextView>(R.id.toast_grey_template_tv).setText(message)

        val toast = Toast(context)
        toast.view = layout
        toast.duration = LENGTH_SHORT
        toast.setGravity(Gravity.BOTTOM, 0, 300)
        toast.show()
    }

    // 수락 또는 거절 클릭 시 홈 화면으로 이동
    private fun goToHome() {
        (context as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, HomeFragment())
            .commitAllowingStateLoss()
    }

    // 거절하기 API 성공
    override fun successReject() {
        makeToast(R.string.toast_challenge_reject)
        goToHome()
    }

    // 거절하기 API 오류
    override fun failReject(message: String) {
        errorToast(message)
    }

    // 수락하기 API 성공
    override fun successAccept() {
        makeToast(R.string.toast_challenge_accept)
        goToHome()
    }

    // 수락하기 API 오류
    override fun failAccept(message: String) {
        errorToast(message)
    }
}
