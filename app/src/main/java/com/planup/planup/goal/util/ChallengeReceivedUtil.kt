package com.planup.planup.goal.util

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.fragment.app.Fragment
import com.planup.planup.R
import com.planup.planup.databinding.FragmentChallengeReceivedBinding
import com.planup.planup.goal.adapter.AcceptChallengeAdapter
import com.planup.planup.goal.adapter.RejectChallengeAdapter
import com.planup.planup.main.MainActivity
import com.planup.planup.main.home.data.ChallengeReceived
import com.planup.planup.main.home.data.ChallengeReceivedPhoto
import com.planup.planup.main.home.data.ChallengeReceivedTimer
import com.planup.planup.main.home.ui.AskNewPenaltyFragment
import com.planup.planup.main.home.ui.HomeFragment
import com.planup.planup.network.controller.ChallengeController

fun Fragment.challengeReceivedInit(binding: FragmentChallengeReceivedBinding, challengeInfo: ChallengeReceived){
    binding.challengeReceivedCl.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener{
        override fun onGlobalLayout() {
            val height = binding.challengeReceivedCl.height
            binding.challengeReceivedInnerCl.minHeight = height
            binding.challengeReceivedCl.viewTreeObserver.removeOnGlobalLayoutListener(this)
        }

    })


    // 친구 이름
    binding.challengeTitleTv.text =
        getString(R.string.challenge_request_from, challengeInfo.friendName)

    // 목표명
    binding.challengeGoalEnterTv.text = challengeInfo.goalName

    // 1회 분량
    binding.challengeAmountEnterTv.text = challengeInfo.goalAmount

    // 종료일
    binding.challengeDueEnterTv.text = challengeInfo.endDate

    // 기준 기간
    binding.challengeDurationEnterTv.text = challengeInfo.duration

    // 빈도
    binding.challengeFrequencyEnterTv.text = challengeInfo.frequency

    when(challengeInfo){
        is ChallengeReceivedTimer -> {
            // 인증방식
            binding.challengeVerifyEnterIv.setImageResource(R.drawable.ic_timer)
            binding.challengeVerifyEnterTv.setText(R.string.auth_timer)

            binding.challengeTimeCl.visibility = View.VISIBLE
            binding.challengePhotoNumberCl.visibility = View.GONE

            // 타이머 정보
            val totalTime = challengeInfo.targetTime
            val hour = totalTime / 3600
            val minute = (totalTime - hour * 3600) / 60
            val second = totalTime - hour * 3600 - minute * 60
            binding.challengeHourEnterTv.text = hour.toString()
            binding.challengeMinuteEnterTv.text = minute.toString()
            binding.challengeSecondEnterTv.text = second.toString()
        }
        else -> {
            // 인증 방식
            binding.challengeVerifyEnterIv.setImageResource(R.drawable.ic_picture)
            binding.challengeVerifyEnterTv.setText(R.string.auth_photo)

            binding.challengeTimeCl.visibility = View.VISIBLE
            binding.challengePhotoNumberCl.visibility = View.GONE

            // 타이머 정보
            binding.challengePhotoNumberEnterTv.text = (challengeInfo as ChallengeReceivedPhoto).howMany.toString()
        }
    }

    // 페널티
    when (challengeInfo.penalty) {
        "coffee" -> {
            binding.challengePenaltyEnterIv.setImageResource(R.drawable.ic_coffee)
            binding.challengePenaltyEnterTv.setText(R.string.challenge_penalty_coffee)
        }

        "snack" -> {
            binding.challengePenaltyEnterIv.setImageResource(R.drawable.ic_burrito)
            binding.challengePenaltyEnterTv.setText(R.string.challenge_penalty_snack)
        }

        "cleaning" -> {
            binding.challengePenaltyEnterIv.setImageResource(R.drawable.ic_bucket)
            binding.challengePenaltyEnterTv.setText(R.string.challenge_penalty_cleaning)
        }

        "wish" -> {
            binding.challengePenaltyEnterIv.setImageResource(R.drawable.ic_megaphone)
            binding.challengePenaltyEnterTv.setText(R.string.challenge_penalty_wish)
        }

        "present" -> {
            binding.challengePenaltyEnterIv.setImageResource(R.drawable.ic_money)
            binding.challengePenaltyEnterTv.setText(R.string.challenge_penalty_present)
        }

        "skip" -> {
            binding.challengePenaltyEnterIv.setImageResource(R.drawable.img_none_friend)
            binding.challengePenaltyEnterTv.setText(R.string.challenge_penalty_skip)
        }

        else -> {
            binding.challengePenaltyEnterIv.setImageResource(R.drawable.ic_pencil)
            binding.challengePenaltyEnterTv.text = challengeInfo.penalty
        }
    }
}

fun FragmentChallengeReceivedBinding.initClickListener(activity: MainActivity, challengeInfo: ChallengeReceived,fragment: Fragment) {
    this.backIv.setOnClickListener {
        Log.d("backIv", "backIv Clicked")
        (activity).supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, HomeFragment())
            .commitAllowingStateLoss()
    }

    this.btnRejectTv.setOnClickListener {
        val service = ChallengeController().apply { setRejectChallengeAdapter(fragment as RejectChallengeAdapter) }
        service.rejectChallenge(challengeInfo.userId, challengeInfo.challengeId)
    }
    this.btnAcceptTv.setOnClickListener {
        val service = ChallengeController().apply { setAcceptChallengeAdapter(fragment as AcceptChallengeAdapter) }
        service.acceptChallenge(challengeInfo.challengeId, challengeInfo.userId)
    }
    this.btnAskNewPenaltyTv.setOnClickListener {
        val askNewPenaltyFragment = AskNewPenaltyFragment().apply {
            arguments = Bundle().apply {
                putInt("userId", challengeInfo.userId)
                putInt("challengeId", challengeInfo.challengeId)
                putSerializable("friendIdList", ArrayList(challengeInfo.friendId))
                putString("friendName", challengeInfo.friendName)
            }
        }
        (activity).supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, askNewPenaltyFragment)
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }
}
// 수락 또는 거절 토스트 메시지 출력
fun FragmentChallengeReceivedBinding.makeToast(message: Int) {
    val inflater = LayoutInflater.from(this.root.context)
    val layout = inflater.inflate(R.layout.toast_grey_template, null)
    layout.findViewById<TextView>(R.id.toast_grey_template_tv).setText(message)
    Toast(this.root.context).apply {
        view = layout
        duration = LENGTH_SHORT
        setGravity(Gravity.BOTTOM, 0, 300)
    }.show()
}
// API 연동 오류 시 토스트 메시지 출력
fun FragmentChallengeReceivedBinding.errorToast(message: String) {
    val inflater = LayoutInflater.from(this.root.context)
    val layout = inflater.inflate(R.layout.toast_grey_template, null)
    layout.findViewById<TextView>(R.id.toast_grey_template_tv).text = message
    Toast(this.root.context).apply {
        view = layout
        duration = LENGTH_SHORT
        setGravity(Gravity.BOTTOM, 0, 477)
    }.show()
}

