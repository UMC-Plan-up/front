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
import com.planup.planup.goal.util.errorToast
import com.planup.planup.goal.util.initClickListener
import com.planup.planup.goal.util.makeToast
import com.planup.planup.main.home.data.ChallengeReceivedPhoto
import com.planup.planup.main.MainActivity

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
        // 전달받은 챌린지 정보
        challengeInfo =
            requireArguments().getParcelable("receivedChallenge")!!
        // 챌린지 정보 초기화
        challengeReceivedInit(binding, challengeInfo)
        binding.initClickListener(requireActivity() as MainActivity, challengeInfo,this)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.apply {

        }
    }

    // 수락 또는 거절 클릭 시 홈 화면으로 이동
    private fun goToHome() {
        (requireActivity() as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, HomeFragment())
            .commitAllowingStateLoss()
    }

    // 거절하기 API 성공
    override fun successReject() {
        binding.makeToast(R.string.toast_challenge_reject)
        goToHome()
    }

    // 거절하기 API 오류
    override fun failReject(message: String) {
        binding.errorToast(message)
    }

    // 수락하기 API 성공
    override fun successAccept() {
        binding.makeToast(R.string.toast_challenge_accept)
        goToHome()
    }

    // 수락하기 API 오류
    override fun failAccept(message: String) {
        binding.errorToast(message)
    }
}
