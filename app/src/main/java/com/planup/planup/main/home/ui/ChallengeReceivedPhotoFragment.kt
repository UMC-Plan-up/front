package com.planup.planup.main.home.ui

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.fragment.app.Fragment
import com.planup.planup.R
import com.planup.planup.databinding.FragmentChallengeReceivedBinding
import com.planup.planup.goal.adapter.AcceptChallengeAdapter
import com.planup.planup.goal.adapter.RejectChallengeAdapter
import com.planup.planup.goal.util.challengeReceivedInit
import com.planup.planup.main.home.data.ChallengeReceivedPhoto
import com.planup.planup.main.MainActivity
import com.planup.planup.network.controller.ChallengeController

class ChallengeReceivedPhotoFragment :
    Fragment(),
    RejectChallengeAdapter,
    AcceptChallengeAdapter {

    private lateinit var binding: FragmentChallengeReceivedBinding

    // 챌린지 정보 저장
    private lateinit var challengeInfo: ChallengeReceivedPhoto

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChallengeReceivedBinding.inflate(inflater, container, false)
        init()
        clickListener()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.apply {

        }
    }

    private fun init() {
        // 전달받은 챌린지 정보
        challengeInfo =
            requireArguments().getParcelable("receivedChallenge")!!
        // 챌린지 정보 초기화
        challengeReceivedInit(binding, challengeInfo)
    }

    private fun clickListener() {
        binding.btnRejectTv.setOnClickListener {
            val service = ChallengeController().apply { setRejectChallengeAdapter(this@ChallengeReceivedPhotoFragment) }
            service.rejectChallenge(challengeInfo.userId, challengeInfo.challengeId)
        }
        binding.btnAcceptTv.setOnClickListener {
            val service = ChallengeController().apply { setAcceptChallengeAdapter(this@ChallengeReceivedPhotoFragment) }
            service.acceptChallenge(challengeInfo.challengeId, challengeInfo.userId)
        }
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
        Toast(context).apply {
            view = layout
            duration = LENGTH_SHORT
            setGravity(Gravity.BOTTOM, 0, 477)
        }.show()
    }

    // 수락 또는 거절 토스트 메시지 출력
    private fun makeToast(message: Int) {
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.toast_grey_template, null)
        layout.findViewById<TextView>(R.id.toast_grey_template_tv).setText(message)
        Toast(context).apply {
            view = layout
            duration = LENGTH_SHORT
            setGravity(Gravity.BOTTOM, 0, 300)
        }.show()
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
